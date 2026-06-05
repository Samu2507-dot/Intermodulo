# 🏨 Roomly 

¡Hola! Somos **Samuel, David y Paula** y como miniproyecto hemos creado a **Roomly**: una aplicación en Java diseñada para gestionar alojamientos, huéspedes, anfitriones y servicios de mantenimiento sin que nada explote por el camino.

El proyecto cuenta con persistencia de datos real, seguridad criptográfica para las contraseñas y un sistema de control de errores a prueba de bombas.

---

## 🛠️ La Anatomía del Proyecto

El código está dividido en estos paquetes:
### 1. Entidades (`entidades`)
* Aquí viven los datos puros (`Usuario`, `Huesped`, `Anfitrion`, `Alojamiento`, `Reserva`...). Son mapeos de **JPA/Hibernate** directos a las tablas.

### 2. Excepciones (`excepciones`)
Creamos 3 excepciones personalizadas de tipo *Checked* (de las que te obligan a poner el `try-catch` sí o sí). Llevan constructores vacíos, con mensaje customizado y tienen **encadenamiento de causas** (`Throwable causa`) para no perder la causa.
* **`AutenticacionException`**: Para cuando alguien mete mal la contraseña o intenta colarse donde no debe.
* **`ReservaInvalidaException`**: El muro de contención si intentan reservar una fecha de salida antes de la de entrada o si los números no cuadran.
* **`MantenimientoException`**: Para controlar los partes técnicos y que no se asignen operarios fantasmas.

### 3. Servicios (`servicios`)
* El cerebro de la operación. Aquí está la lógica de negocio real y la comunicación con el `EntityManager`. 
* Metimos **validaciones tempranas** para frenar los pies al usuario antes de romper nada.
* Controla el inicio de sesión seguro y el registro usando **BCrypt** para que las contraseñas no se guarden en texto plano (seguridad ante todo).

### 4. Clase Principal (`Principal.java`)
* La clase que gestiona todas las operaciones
* Está gestionado con un **Try-With-Resources** que abre el `EntityManagerFactory` y el `EntityManager`, y se encarga de cerrarlos automáticamente sin dejar conexiones colgadas por ahí (adiós fugas de memoria).
* Captura los errores de forma ordenada: primero va a por nuestras excepciones específicas y si no, ya cae en la genérica.

---

## 🧪 Pruebas Unitarias (`src/test/java`)

Para dormir tranquilos por las noches, montamos unas pruebas con **JUnit 5**:
* **Solo a los servicios**: Nos enfocamos únicamente en el paquete `servicios`. Testear las excepciones solas o los *getters* de las entidades no tenía ningún sentido.
* **A prueba de fallos**: Probamos el "camino feliz" (que todo guarde bien y BCrypt encripte) y también los caminos oscuros, para obligar al código a lanzar nuestras excepciones cuando le metemos datos corruptos.

> 🟢 **Estado actual:** El 100% de los tests corren en un verde brillante precioso. Cero fallos.

---

## 🧰 Herramientas de Infraestructura

Para que el proyecto volase de verdad y no se quedase solo en nuestros ordenadores, nos hemos repartido el trabajo duro de sistemas:

* **Java JDK** (El motor de todo)
* **JPA / Hibernate** (Para hablar con la base de datos)
* **JUnit 5** (Nuestro detector de errores automatizado)
* **BCrypt** (El candado para las contraseñas)
* **Diseño de Base de Datos Relacional**: Modelamos desde cero todo el esquema de tablas, claves primarias y relaciones foráneas para asegurar que los datos de alojamientos y usuarios fuesen consistentes y eficientes.
* **Despliegue en la Nube (AWS)**: Configurado y desplegado en un servidor de Amazon Web Services (AWS) para que la infraestructura sea real, accesible y escalable en producción.