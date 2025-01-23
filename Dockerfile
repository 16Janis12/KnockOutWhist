FROM sbtscala/scala-sbt:eclipse-temurin-21.0.5_11_1.10.6_3.5.2

WORKDIR /knockout

COPY build.sbt /knockout/
COPY project /knockout/project/

RUN sbt update

COPY . /knockout

RUN apt-get update
RUN apt-get install -y libgtk-3-0 libglib2.0-0 libxext6 libxrender1 libxtst6 libx11-6 xvfb libswt-gtk-3-java gkt3



ENV DISPLAY=host.docker.internal:0.0

RUN sbt compile

CMD sbt run
