set -e

####################################################

## 1 - spring backend
#cleanup
rm -rf  spring-backend weather-app-rest-api


#2 - create image for
mkdir spring-backend
cd spring-backend
cat > "Dockerfile" << 'EOL'
FROM eclipse-temurin:21-jre-alpine

EXPOSE 8080

WORKDIR /app
COPY weather-rest-api.jar ./

ENTRYPOINT ["java", "-jar", "weather-rest-api.jar"]
EOL
cd ..

#1.1 - create weather rest api jar
git clone https://github.com/MrShoffen/weather-app-rest-api.git
cd weather-app-rest-api
sudo chmod +x ./gradlew
./gradlew bootJar
cp build/libs/weather-rest-api.jar ../spring-backend/
cd ..
rm -rf weather-app-rest-api

#1.2 - run docker image

cd spring-backend
docker build -t spring-boot-weather-rest-api:1 .
cd ..

####################################
#####################################

# 2 cities api

#cleanup
rm -rf  cities cities_api

#2 - create image for
mkdir cities
cd cities
cat > "Dockerfile" << 'EOL'
FROM eclipse-temurin:21-jre-alpine

EXPOSE 8080

WORKDIR /app
COPY cities-api.jar ./

ENTRYPOINT ["java", "-jar", "cities-api.jar"]
EOL
cd ..

#2.1 - create weather rest api jar
git clone https://github.com/MrShoffen/cities_api.git
cd cities_api
sudo chmod +x ./gradlew
./gradlew bootJar
cp build/libs/cities-api.jar ../cities/
cd ..
rm -rf cities_api

#2.2 - run docker image

cd cities
docker build -t cities_autofill_api:1 .
cd ..


#####################################
#####################################

# 3 react front

#cleanup
rm -rf react-weather-frontend weather-app-react-frontend


###################################################
#3 - react frontend
mkdir react-weather-frontend
cd react-weather-frontend

cat > "nginx.conf" << 'EOL'

server {
    listen 80;

    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri /index.html;
                                proxy_set_header Host $host;

    }

}

EOL


cat > "Dockerfile" << 'EOL'
FROM node:22-alpine3.20 as build
WORKDIR /app

COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
EOL
cd ..

#3.1 - create static
git clone https://github.com/MrShoffen/weather-app-react-frontend.git
cd weather-app-react-frontend
cp -r * ../react-weather-frontend
cd ..
rm -rf weather-app-react-frontend

#2.2 - run docker image
cd react-weather-frontend
docker build -t weather-react-frontend:1 .
cd ..

#############################################
####################################


# 4. Proxy


#cleanup
rm -rf nginx-proxy

######################################################
#4 - create nginx

mkdir nginx-proxy
cd nginx-proxy

cat > "nginx.conf" << 'EOL'
events{}
http {

        upstream backend-spring {
                server weather-rest-api:8080;
        }

        upstream cities {
                server cities-autofill:8081;
        }

        upstream frontend-react {
                server weather-rest-frontend:80;
        }


        server {
                listen 80;

                client_max_body_size 10M;

                location /weather/api/ {
                        proxy_pass http://backend-spring/weather/api/;
                        proxy_set_header Host $host;
                        proxy_set_header X-Real-IP $remote_addr;

                }

                location /cities-autofill-api/ {
                        proxy_pass http://cities/cities-autofill-api;
                        proxy_set_header Host $host;
                        proxy_set_header X-Real-IP $remote_addr;
                }

                location /weather-app/ {
                        proxy_pass http://frontend-react/;
                        proxy_set_header Host $host;
                        proxy_set_header X-Real-IP $remote_addr;

                }
        }
}

EOL



cat > "Dockerfile" << 'EOL'
FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
EOL

docker build -t nginx-proxy:1 .
cd ..



####################################
#############################

# 5. Final compose

rm -rf docker-compose.yml

cat > "docker-compose.yml" << 'EOL'
services:

  weather-rest-api:
    container_name: spring-weather-rest
    image: spring-boot-weather-rest-api:1
    env_file:
    - ./vars.env
    depends_on:
    - database

  cities-autofill:
    container_name: cities-autofill-api
    image: cities_autofill_api:1

  weather-rest-frontend:
    container_name: react-weather-frontend
    image: weather-react-frontend:1


  database:
    container_name: weather_database
    image: postgres:17
    env_file:
    - ./vars.env

  nginx:
    container_name: nginx_proxy
    image: nginx-proxy:1
    ports:
    - "80:80"
    depends_on:
    - weather-rest-api
    - cities-autofill

EOL

docker compose up -d



