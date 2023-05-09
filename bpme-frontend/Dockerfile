FROM node:18-ubuntu:latest
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
CMD ["npm", "run" , "dev"]
EXPOSE 3000
