FROM openjdk:11-jdk-slim
COPY h2-1.4.200.jar app.jar 
EXPOSE 3000 3001
CMD java -cp app.jar org.h2.tools.Server -webPort 3000  -tcpPort 3001 -webAllowOthers -tcpAllowOthers -ifNotExists