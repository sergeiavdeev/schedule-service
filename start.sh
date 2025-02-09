mvn clean package -Dmaven.test.skip=true
docker ps -aq --filter "name=schedule-service" | xargs -r docker rm -f
docker system prune -a -f
docker build -t sergeiavdeev/schedule-service:1-dev .
PROFILE=dev TAG=1-dev docker compose up -d
docker system prune -a -f