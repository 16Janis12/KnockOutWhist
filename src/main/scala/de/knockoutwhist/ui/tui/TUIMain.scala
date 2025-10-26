package de.knockoutwhist.ui.tui

import de.knockoutwhist.cards.{Card, CardValue, Hand, Suit}
import de.knockoutwhist.control.GameState.{Lobby, MainMenu}
import de.knockoutwhist.control.controllerBaseImpl.BaseGameLogic
import de.knockoutwhist.control.controllerBaseImpl.sublogic.util.{MatchUtil, PlayerUtil}
import de.knockoutwhist.control.sublogic.PlayerTieLogic
import de.knockoutwhist.control.{ControlThread, GameLogic}
import de.knockoutwhist.events.*
import de.knockoutwhist.events.global.*
import de.knockoutwhist.events.global.tie.*
import de.knockoutwhist.events.player.{RequestCardEvent, RequestTieChoiceEvent, RequestTrumpSuitEvent}
import de.knockoutwhist.player.Playertype.HUMAN
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory}
import de.knockoutwhist.ui.UI
import de.knockoutwhist.undo.UndoneException
import de.knockoutwhist.undo.commands.{PlayCardCommand, PlayDogCardCommand, SelectTieNumberCommand, SelectTrumpSuitCommand}
import de.knockoutwhist.utils.CustomThread
import de.knockoutwhist.utils.events.{EventListener, SimpleEvent}

import java.io.{BufferedReader, InputStreamReader}
import java.util.concurrent.atomic.AtomicBoolean
import scala.annotation.tailrec
import scala.util.boundary.break
import scala.util.{Failure, Success, Try, boundary}

class TUIMain extends CustomThread with EventListener with UI {

  var logic: Option[GameLogic] = None

  setName("TUI")
  
  var init = false

  override def instance: CustomThread = this

  override def runLater[R](op: => R): Unit = {
    interrupted.set(true)
    super.runLater(op)
  }

  override def listen(event: SimpleEvent): Unit = {
    runLater {
      event match {
        case event: MatchEndEvent =>
          TUIUtil.clearConsole()
          println(s"The match has ended! The winner is ${event.winner.name}.")
        case event: NewRoundEvent =>
          TUIUtil.clearConsole()
          println(s"Starting a new round...")
        case event: NewTrickEvent =>
          TUIUtil.clearConsole()
          println(s"Starting a new trick...")
        case event: RoundEndEvent =>
          TUIUtil.clearConsole()
          println(s"The round has ended! The winner is ${event.winner.name}.")
        case event: ShowDogsEvent =>
          TUIUtil.clearConsole(2)
          println(s"Dogs: ${event.dogs.map(_.name).mkString(", ")}")
        case event: ShowPlayersOutEvent =>
          TUIUtil.clearConsole(2)
          println(s"Eliminated players: ${event.out.map(_.name).mkString(", ")}")
        case event: TrickEndEvent =>
          TUIUtil.clearConsole(4)
          println(s"The trick has ended! The winner is ${event.winner.name}.")
        case event: TrumpSelectEvent =>
          TUIUtil.clearConsole(2)
          println(s"${event.player.name} will select the next trump suit.")
        case event: TurnEvent =>
          boundary {
            if (logic.get.getCurrentTrick.isEmpty) {
              println("No trick found!")
              break()
            }
            val trickImpl = logic.get.getCurrentTrick.get
            if (logic.get.getCurrentRound.isEmpty) {
              println("No round found!")
              break()
            }
            val roundImpl = logic.get.getCurrentRound.get
            TUIUtil.clearConsole()
            val sb = new StringBuilder()
            sb.append("Current Trick:\n")
            sb.append("Trump-Suit: " + roundImpl.trumpSuit + "\n")
            if (trickImpl.firstCard.isDefined) {
              sb.append(s"Suit to play: ${trickImpl.firstCard.get.suit}\n")
            }
            for ((card, player) <- trickImpl.cards) {
              sb.append(s"${player.name} played ${card.toString}\n")
            }
            println(sb.toString())
            println(s"It is now ${event.player.name}'s turn.")
          }
        case event: TieAllPlayersSelectedEvent =>
          TUIUtil.clearConsole(2)
          println(s"All players have selected their tie cards.")
        case event: TieEvent =>
          TUIUtil.clearConsole(2)
          println(s"It's a tie. Let's cut to determine the winner. Players involved: ${event.players.map(_.name).mkString(", ")}")
        case event: TieShowPlayerCardsEvent =>
          TUIUtil.clearConsole(2)
          println(s"Players' tie cards:")
          showtiecardseventmethod(event)
        case event: TieTieEvent =>
          TUIUtil.clearConsole(2)
          println(s"It's still a tie after the tie-breaker. Another tie-breaker will be held.")
          println(s"This time, players involved: ${event.players.map(_.name).mkString(", ")}")
        case event: TieTurnEvent =>
          TUIUtil.clearConsole(2)
          println(s"It is now ${event.player.name}'s turn to select a tie card.")
        case event: TieWinningPlayersEvent =>
          TUIUtil.clearConsole(2)
          println(s"The winner(s) of the tie-breaker: ${event.winners.map(_.name).mkString(", ")}")
        case event: RequestTieChoiceEvent =>
          reqnumbereventmet(event)
        case event: RequestCardEvent =>
          if (event.player.isInDogLife) reqdogeventmet(event)
          else reqcardeventmet(event)
        case event: RequestTrumpSuitEvent =>
          reqpicktevmet(event)
        case event: GameStateChangeEvent =>
          if (event.newState == MainMenu) {
            mainMenu()
          } else if (event.newState == Lobby) {
            reqplayersevent()
          }
          Some(true)
        case _ => None
      }
    }
  }
  
  
  private object TUICards {
    def renderCardAsString(card: Card): Vector[String] = {
      val lines = "│         │"
      if (card.cardValue == CardValue.Ten) {
        return Vector(
          s"┌─────────┐",
          s"│${cardColour(card.suit)}${Console.BOLD}${card.cardValue.cardType()}${Console.RESET}       │",
          lines,
          s"│    ${cardColour(card.suit)}${Console.BOLD}${card.suit.cardType()}${Console.RESET}    │",
          lines,
          s"│       ${cardColour(card.suit)}${Console.BOLD}${card.cardValue.cardType()}${Console.RESET}│",
          s"└─────────┘"
        )
      }
      Vector(
        s"┌─────────┐",
        s"│${cardColour(card.suit)}${Console.BOLD}${card.cardValue.cardType()}${Console.RESET}        │",
        lines,
        s"│    ${cardColour(card.suit)}${Console.BOLD}${card.suit.cardType()}${Console.RESET}    │",
        lines,
        s"│        ${cardColour(card.suit)}${Console.BOLD}${card.cardValue.cardType()}${Console.RESET}│",
        s"└─────────┘"
      )
    }
    
    private[ui] def cardColour(suit: Suit): String = suit match {
      case Suit.Hearts | Suit.Diamonds => Console.RED
      case Suit.Clubs | Suit.Spades => Console.BLACK
    }
    
    def renderHandEvent(hand: Hand, showNumbers: Boolean): Vector[String] = {
      val cardStrings = hand.cards.map(TUICards.renderCardAsString)
      var zipped = cardStrings.transpose
      if (showNumbers) zipped = {
        List.tabulate(hand.cards.length) { i =>
          s"     ${i + 1}     "
        }
      } :: zipped
      zipped.map(_.mkString(" ")).toVector
    }
  }
  private object TUIUtil {
    def clearConsole(lines: Int = 32): Int = {
      var l = 0
      for (_ <- 0 until lines) {
        println()
        l += 1
      }
      l
    }
  }

  override def initial(gameLogic: GameLogic): Boolean = {
    if (init) {
      return false
    }
    init = true
    this.logic = Some(gameLogic)
    start()
    runLater(mainMenu())
    true
  }

  @tailrec
  private def mainMenu(): Unit = {
    TUIUtil.clearConsole()
    println("Welcome to Knockout Whist!")
    println()
    println("Please select an option:")
    println("1. Start a new match")
    println("2. Exit")
    Try {
      input().toInt
    } match {
      case Success(value) =>
        value match {
          case 1 =>
            ControlThread.runLater {
              logic.get.createSession()
            }
          case 2 =>
            println("Exiting the game.")
            ControlThread.runLater {
              logic.get.endSession()
            }
          case _ =>
            mainMenu()
        }
      case Failure(exception) =>
        exception match
          case undo: UndoneException =>
          case _ =>
            mainMenu()
    }
  }

  private def showtiecardseventmethod(event: TieShowPlayerCardsEvent): Option[Boolean] = {
    val a: Array[String] = Array("", "", "", "", "", "", "", "")
    for ((player, card) <- logic.get.playerTieLogic.getSelectedCard) {
      val playerNameLength = player.name.length
      a(0) += " " + player.name + ":" + (" " * (playerNameLength - 1))
      val rendered = TUICards.renderCardAsString(card)
      a(1) += " " + rendered(0)
      a(2) += " " + rendered(1)
      a(3) += " " + rendered(2)
      a(4) += " " + rendered(3)
      a(5) += " " + rendered(4)
      a(6) += " " + rendered(5)
      a(7) += " " + rendered(6)
    }
    a.foreach(println)
    Some(true)
  }

  private def reqnumbereventmet(event: RequestTieChoiceEvent): Option[Boolean] = {
    println(s"${event.player.name}, please select a number between 1 and ${logic.get.playerTieLogic.highestAllowedNumber()} for your tie-breaker card:")
    val tryTie = Try {
      val number = input().toInt
      if (number < 1 || number > logic.get.playerTieLogic.highestAllowedNumber()) {
        throw new IllegalArgumentException(s"Number must be between 1 and ${logic.get.playerTieLogic.highestAllowedNumber()}")
      }
      number
    }
    if(tryTie.isFailure && tryTie.failed.get.isInstanceOf[UndoneException]) {
      return Some(true)
    }

    if (tryTie.isFailure) {
      println("Invalid input. Please try again.")
      return reqnumbereventmet(event)
    }

    ControlThread.runLater {
      logic.get.undoManager.doStep(
        SelectTieNumberCommand(
          logic.get.createSnapshot(),
          logic.get.playerTieLogic.createSnapshot(),
          tryTie.get
        )
      )
    }
    Some(true)
  }

  private def reqcardeventmet(event: RequestCardEvent): Option[Boolean] = {
    println("Which card do you want to play?")
    if (logic.isEmpty) throw new IllegalStateException("Logic is not initialized")
    val logicImpl = logic.get
    if (logicImpl.getCurrentRound.isEmpty) throw new IllegalStateException("No round found!")
    val roundImpl = logicImpl.getCurrentRound.get
    if (logicImpl.getCurrentTrick.isEmpty) throw new IllegalStateException("No trick found!")
    val trickImpl = logicImpl.getCurrentTrick.get
    if (event.player.currentHand().isEmpty) {
      println("You have no cards to play! This should not happen.")
      return Some(true)
    }
    val hand = event.player.currentHand().get
    TUICards.renderHandEvent(hand, true).foreach(println(_))
    val tryCard = Try {
      val card = input().toInt - 1
      if (card < 0 || card >= hand.cards.length) {
        throw new IllegalArgumentException(s"Number has to be between 1 and ${hand.cards.length}")
      } else {
        hand.cards(card)
      }
    }
    if (tryCard.isFailure && tryCard.failed.get.isInstanceOf[UndoneException]) {
      return Some(true)
    }

    if (tryCard.isFailure) {
      println("Invalid input. Please try again.")
      return reqcardeventmet(event)
    }

    if (!PlayerUtil.canPlayCard(tryCard.get, roundImpl, trickImpl, event.player)) {
      println("You cannot play this card. The suit to follow is " +
        s"${if (trickImpl.firstCard.isDefined) trickImpl.firstCard.get.suit.toString else "N/A"}. " +
        s"You have to follow suit if you can.")
      return reqcardeventmet(event)
    }

    ControlThread.runLater {
      logic.get.undoManager.doStep(
        PlayCardCommand(
          logic.get.createSnapshot(),
          logic.get.playerTieLogic.createSnapshot(),
          tryCard.get
        )
      )
    }
    Some(true)
  }

  private def reqdogeventmet(event: RequestCardEvent): Option[Boolean] = {
    println("You are using your dog life. Do you want to play your final card now?")
    if (event.player.currentHand().isEmpty) {
      println("You have no cards to play! This should not happen.")
      return Some(true)
    }
    val hand = event.player.currentHand().get
    if (logic.get.getCurrentMatch.isEmpty) {
      println("No match found!")
      return Some(true)
    }
    val matchImpl = logic.get.getCurrentMatch.get
    if (logic.get.getCurrentRound.isEmpty) {
      println("No round found!")
      return Some(true)
    }
    val roundImpl = logic.get.getCurrentRound.get
    val needstoplay = MatchUtil.dogNeedsToPlay(matchImpl, roundImpl)

    if (needstoplay) {
      println("You have to play your final card this round!")
    } else {
      println("Do you want to play your final card? (y/n)")
    }

    TUICards.renderHandEvent(hand, false).foreach(println(_))
    val tryDogCard = Try {
      val card = input()
      if (card.equalsIgnoreCase("y")) {
        Some(hand.cards.head)
      } else if (card.equalsIgnoreCase("n") && !needstoplay) {
        None
      } else {
        throw new IllegalArgumentException("Didn't want to play card but had to")
      }
    }
    if (tryDogCard.isFailure && tryDogCard.failed.get.isInstanceOf[UndoneException]) {
      return Some(true)
    }

    if (tryDogCard.isFailure) {
      println("Invalid input. Please try again.")
      return reqdogeventmet(event)
    }

    ControlThread.runLater {
      logic.get.undoManager.doStep(
        PlayDogCardCommand(
          logic.get.createSnapshot(),
          logic.get.playerTieLogic.createSnapshot(),
          tryDogCard.get
        )
      )
    }
    Some(true)
  }

  private def reqplayersevent(): Option[Boolean] = {
    println("Please enter the names of the players, separated by a comma.")
    val names = Try {input().split(",")}
    if (names.isFailure && names.failed.get.isInstanceOf[UndoneException]) {
      return Some(true)
    }
    if (names.get.length < 2) {
      println("Please enter at least two names.")
      return reqplayersevent()
    }
    if (names.get.distinct.length != names.get.length) {
      println("Please enter unique names.")
      return reqplayersevent()
    }
    if (names.get.count(_.trim.isBlank) > 0
      || names.get.count(_.trim.length <= 2) > 0
      || names.get.count(_.trim.length > 10) > 0) {
      println("Please enter valid names. Those can not be empty, shorter than 2 or longer then 10 characters.")
      return reqplayersevent()
    }
    ControlThread.runLater {
      logic.get.createMatch(names.get
        .map(s => PlayerFactory.createPlayer(s, playertype = HUMAN))
        .toList)
      logic.get.controlMatch()
    }
    Some(true)
  }

  private def reqpicktevmet(event: RequestTrumpSuitEvent): Option[Boolean] = {
    println(s"${event.player.name}, please select a trump suit:")
    println("1. Hearts")
    println("2. Diamonds")
    println("3. Clubs")
    println("4. Spades")
    println()
    println("Your cards are:")
    if (event.player.currentHand().isEmpty) {
      println("You have no cards! This should not happen.")
      return Some(true)
    }
    val hand = event.player.currentHand().get
    TUICards.renderHandEvent(hand, false).foreach(println(_))
    println()
    val trySuit = Try {
      val suit = input().toInt
      suit match {
        case 1 => Suit.Hearts
        case 2 => Suit.Diamonds
        case 3 => Suit.Clubs
        case 4 => Suit.Spades
        case _ => throw IllegalArgumentException("Didn't enter a number between 1 and 4")
      }
    }
    if (trySuit.isFailure && trySuit.failed.get.isInstanceOf[UndoneException]) {
      return Some(true)
    }
    if (trySuit.isFailure) {
      println("Invalid input. Please try again.")
      return reqpicktevmet(event)
    }
    ControlThread.runLater {
      logic.get.undoManager.doStep(
        SelectTrumpSuitCommand(
          logic.get.createSnapshot(),
          logic.get.playerTieLogic.createSnapshot(),
          trySuit.get
        )
      )
    }
    Some(true)
  }

  private val isInIO: AtomicBoolean = new AtomicBoolean(false)
  private val interrupted: AtomicBoolean = new AtomicBoolean(false)

  private def input(): String = {
    interrupted.set(false)
    val reader = new BufferedReader(new InputStreamReader(System.in))

    while (!interrupted.get()) {
      if (reader.ready()) {
        val in = reader.readLine()
        if (in.equals("undo")) {
          logic.get.undoManager.undoStep()
          throw new UndoneException("Undo")
        } else if (in.equals("redo")) {
          logic.get.undoManager.redoStep()
          throw new UndoneException("Redo")
        }else if(in.equals("load")
          && logic.get.persistenceManager.canLoadfile("currentSnapshot")) {
          ControlThread.runLater {
            logic.get.persistenceManager.loadFile("currentSnapshot")
          }
          throw new UndoneException("Load")
        }else if(in.equals("save")) {
          logic.get.persistenceManager.saveFile("currentSnapshot")
        }
        return in
      }
      Thread.sleep(50)
    }
    throw new UndoneException("Skipped")
  }



}
