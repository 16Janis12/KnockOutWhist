FROM sbtscala/scala-sbt:eclipse-temurin-jammy-22_36_1.10.0_3.4.2

WORKDIR /knockout

RUN apt-get update && apt-get install -y curl gnupg2 x11-apps
RUN apt-get update && apt-get install -y \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxrandr2 \
    libgtk-3-0

RUN curl -sL https://dlcdn.apache.org/sbt/debian/sbt-1.9.4.deb -o sbt.deb

RUN dpkg -i sbt.deb || apt-get install -f -y

RUN sbt update

ENV SBT_OPTS="-Xms512M -Xmx1536M -Xss2M -XX:MaxMetaspaceSize=512M"

COPY . /knockout

RUN sbt compile

CMD sbt run
