FROM node:18-alpine

WORKDIR /app
ENV NODE_ENV=production

COPY package*.json ./
RUN npm install --omit=dev --no-audit --no-fund && npm cache clean --force

COPY app.js ./

EXPOSE 3000

CMD ["npm", "start"]