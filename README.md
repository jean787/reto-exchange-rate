# Informacion del servicio

Proyecto que expone una API RESTFull para la funcionalidad de tipo de cambio

## Prerequisitos

Se debe tener instalado las siguientes herramientas

* *H2* Versión mas reciente
* *Maven* versión 3.8.x o superior
* *Java* version 17
* *Lombok*

### Instalación

1. Clonar el repositorio con el siguiente comando:
 ```bash
   git clone https://github.com/jean787/reto-ibk.git
   ```
2. Abrir en nuestro IDE(Intellij, Eclipse, VS Code, etc):
3. Instalar en el repositorio local, para el cual se realizará el siguiente comando en Maven

```
mvn clean install
```

4. Ejecutar el siguiente comando docker para construir nuestra imagen

```
docker build -t exchange-rate-v1:0.0.1 .
```

5. Ejecutar la imagen

```
docker run -p 9091:9091 exchange-rate-v1:0.0.1 .
```

### Probando la API

1. Ejecutar el endpoint **/auth/v1/jwt** - (POST - Generar token)

### Consideración
Usar la siguiente credencial para generar nuestro token

```json
{
   "username": "user1",
   "password": "hunter"
}
```

Nos deberá de responder con el token generado

```json
{
   "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFucGVyZXpAZ21haWwuY29tIiwiaWF0IjoxNzEyMzQ4NjYxLCJleHAiOjE3MTIzNTAxMDF9.4pMcJw7wjDeaaeC3cvN8To_nZ3_rYVeO4Y2_pTaZpds"
}
```

3. Probar los endpoints

**localhost:9091/exchange-rate/v1/initiate** - (POST - Ejecuta tipo de cambio)

*RequestBody Ejemplo:*
```json
{
   "amount": 100,
   "sourceCurrency": "USD",
   "targetCurrency": "PEN"
}
```

**localhost:9091/exchange-rate/v1/retrieve** - (GET - Lista registro de tipo de cambio)
