FROM node:14.16.0-alpine

RUN mkdir /app
WORKDIR /app

COPY LICENSE .
COPY README.md .
COPY package.json .
COPY build.txt .
COPY ./*.js ./
COPY summaries ./summaries

RUN npm config set @quote-of-the-day:registry=https://gitlab.com/api/v4/projects/32433382/packages/npm/
RUN npm install

EXPOSE 3002

CMD ["node", "--expose-gc", "app.js"]