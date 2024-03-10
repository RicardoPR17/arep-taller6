# **Taller 6 - Patrones Arquitecturales**
### *Hecho por Ricardo Pulido Renteria*

En este taller, se trabaja con Spark, Mongo y Docker para crear un servicio donde almacenar mensajes que sean enviados por los usuarios desde el navegador y mostrar los 10 más recientes. Para esto, se manejan 2 proyectos dentro de este repositorio donde se cuenta con un servicio de almacenamiento y consulta de mensajes y otro que expone el servicio web y actúa como balanceador de cargas usando RoundRobin.

## **Descarga y ejecución**

Para poder ejecutar este proyecto, el cual se ejecutará en tu ambiente local por fines de desarrollo y pruebas, debes contar con algunos elementos que serán indicados a continuación.


## **Prerequisitos**

La ejecución de este proyecto requiere de:
- `Java (versión 17 o superior)`
- `Maven (3.8.1 o superior)`
- `Conexión a internet`
- `Docker Desktop`


## **Instalación**

Para poder trabajar con el proyecto hay 2 opciones, descargarlo desde GitHub o descargar la imagen del proyecto de Docker Hub.

**_Nota:_** Ambas requieren de que tengas corriendo la aplicación de DockerHub.

### GitHub
Sí se descarga desde GitHub, primero se clona el repositorio en su máquina con el comando
```bash
git clone https://github.com/RicardoPR17/arep-taller6.git
```
o puede descargarlo en formato zip y descomprimirlo. Luego, se deben ejecutar los siguientes comandos:
  1. Acceder al directorio del proyecto usando el comando 
```bash
cd arep-taller6
```
  2. Una vez dentro del directorio del proyecto general, se necesita generar la carpeta _target_ en los 2 proyectos contenidos en el repositorio. Para esto, se ejecuta la siguiente serie de comandos
```bash
cd logService
mvn clean install
cd ../roundRobin
mvn clean install
cd ..
```
  3. Contruimos las imágenes de Docker necesarias con los comandos
    
```bash
docker build -t roundrobin:latest ./roundRobin
docker build -t logservice:latest ./logService
```

### Docker Hub

En este caso, tenemos que descargar mínimo las imágenes de RoundRobin y de LogService de DockerHub, la de mongo es opcional puesto que con el _docker-compose_ será cargada a tu entorno de forma automática. Dicho esto, para bajar las imágenes mencionadas se usan los comandos

```bash
docker pull ricardopr17/arep-taller6:roundrobin
docker pull ricardopr17/arep-taller6:logservice
```

Para ajustar los nombres para el _docker-compose_ en caso de no querer modificarlo, podemos ejecutar los siguientes comandos para hacer un tag diferente

```bash
docker tag ricardopr17/arep-taller6:roundrobin roundrobin
docker tag ricardopr17/arep-taller6:logservice logservice
```

Con esto, según el camino que se haya decidido, tendríamos todo listo para ejecutar la aplicación.

## **Ejecución**

Para la ejecución, vamos a utilizar el siguiente _docker-compose.yml_

```yaml
version: "1"

services:
  db:
    image: mongo:3.6.1
    container_name: db
    volumes:
      - mongodb:/data/db
      - mongodb_config:/data/configdb
    ports:
      - 27017:27017
    command: mongod
  logs-1:
    image: logservice:latest
    ports:
      - "5000:5000"
    container_name: logs-1
  logs-2:
    image: logservice:latest
    ports:
      - "5001:5000"
    container_name: logs-2
  logs-3:
    image: logservice:latest
    ports:
      - "5002:5000"
    container_name: logs-3
  round:
    image: roundrobin:latest
    ports:
      - "47000:47000"
    container_name: round
  

volumes:
  mongodb:
  mongodb_config:
```

**_Nota:_** En caso de no cambiar los tags de las imágenes, es solo hacer el cambio. Por ejemplo: logservice:latest por ricardopr17/arep-taller6:logservice

Ahora, creamos los contenedores y la red necesaria con el comando
```bash
docker-compose up -d
```

Con esto, se crearán todos los contenedores necesarios tanto para el LogService (que serán 3), para RoundRobin y para MongoDB.

## **Uso**

Accedemos desde la ruta http://localhost:47000/index.html y veremos una página con un campo de texto. Aquí, podemos escribir cualquier mensaje que no esté separado por espacio y dar clic en el botón de _Submit_. Esto enviará nuestro mensaje al servicio y lo almacenará en la base de datos junto a la fecha y hora de recepción, acto seguido mostrará en pantalla un JSON con los 10 mensajes más recientes.


## **Diseño**

Se manejaron 2 proyectos, uno en el directorio _roundRobin_ y otro en el directorio _logService_.

En _roundRobin_, se manejó tanto el servicio de front con el formulario y la conexión hacía las 3 instancias del LogService. Al manejar 3 instancias y por el nombre del directorio, se busca hacer un balanceador de cargas donde cada petición es enviada a una instancia diferente, yendo de la primera a la tercera y vuelve a la primera. Con esto, no se satura ninguna instancia con muchas peticiones seguidas.

Con el método de `location` se asigna la carpeta dentro del directorio _resources_ donde buscar los archivos estáticos que sean solicitados. En este caso, la carpeta se llama _public_ y en ella tenemos los archivos del formulario junto a su respectiva hoja de estilos y scripts.

Las respuestas recibidas por las instancias de LogService se presentan como

```JSON
{
  "1": {
    "date": "fecha de envío del mensaje",
    "message": "mensaje enviado"
  },
  "2" : ...,
   ⋮
  "n" : ...
}
```

Este "_n_" es porque, en caso de ser los primeros mensajes, no se encontrarán 10 mensajes en la base de datos, por lo cual muestra los existentes y se limita a 10 al presentarlos cumpliendo siempre con ser los más recientes.

Por su parte, _logService_ maneja las funcionalidades de almacenamiento y consulta de registros. Está diseñado para conectarse a la base de datos no relacional de MongoDB de la red creada, allí almacenar en la colección "_logs_" de la base de datos "_taller6_" los documentos con el mensaje y el usuario, cumpliendo el esquema
```JSON
{
  "message": "mensaje almacenado",
  "date":  "fecha de envío"
}
```

## **Pruebas**

Para estas pruebas, vamos a acceder a la ruta http://localhost:47000/index.html usando el navegador de Firefox y veremos las peticiones realizadas en el apartado de red de su inspección de recursos.

+ Primero, iniciamos el servicio ejecutando el comando de docker-componse mostrado anteriormente. Esto, nos creará un grupo de contenedores dentro de una misma red

![Comando compose](<Imágenes README/comando compose.png>)

![Docker compose](<Imágenes README/compose.png>)

+ Ahora, si accedemos a la ruta mencionada, veremos la siguiente página

![Formulario](<Imágenes README/formulario.png>)

+ Enviando un primer mensaje diciendo AREP, veremos que solo habrá un elemento en el JSON de respuesta

![Primer mensaje](<Imágenes README/mensaje1.png>)

+ Enviando otros mensajes, llegando a los 10 exactos, veremos que la respuesta los muestra correctamente y nuestro mensaje anterior es el último en la lista

![10 mensajes](<Imágenes README/mensajes10.png>)

+ Si enviamos uno nuevo, en este caso el mensaje será Parcial, veremos que el primer de AREP dejará de aparecer en la respuesta

![11 mensajes](<Imágenes README/mensaje11.png>)

+ Al revisar la base de datos, veremos que aún se encuentra almacenado el mensaje de AREP, pero por la condición de mostrar los 10 más recientes no es mostrado como respuesta del formulario

![Muestra DB](<Imágenes README/muestraDB.png>)

## **Despliegue en AWS**

A continuación, se muestra la operación de este servicio de mensajes en una instancia EC2 de AWS:

