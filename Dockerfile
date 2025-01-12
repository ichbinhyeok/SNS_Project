FROM gradle:7.6.1-jdk17
WORKDIR /app
COPY . .
EXPOSE 8080
ENTRYPOINT ["gradle", "bootRun"]