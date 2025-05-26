# Usa una imagen oficial de node
FROM node:18-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar package.json y package-lock.json para instalar dependencias
COPY package*.json ./

# Instalar dependencias
RUN npm install

# Copiar el resto de archivos al contenedor
COPY . .

# Exponer el puerto de Vite (por defecto 5173)
EXPOSE 5173

# Comando para arrancar el servidor de desarrollo de Vite
CMD ["npm", "run", "dev"]
