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
* **`ReservaInvalidaException`**: El muro de contención si intentan reservar una fecha de salida antes de la de entrada o si los números no cuadran
* **`MantenimientoException`**: Para controlar los partes técnicos y que no se asignen operarios fantasmas.

### 3. Servicios (`servicios`)
* El cerebro de la operación. Aquí está la lógica de negocio real y la comunicación con el `EntityManager`. 
* Metimos **validaciones tempranas** para frenar los pies al usuario antes de romper nada.
* Controla el inicio de sesión seguro y el registro usando **BCrypt** para que las contraseñas no se guarden en texto plano (seguridad ante todo).

### 3. Controladores (`controller`)
* Código limpio: Hemos creado métodos genéricos (setupColumn) para configurar las tablas, evitando repetir el mismo código una y otra vez.
* Interfaz fluida: Usamos Platform.runLater para cargar los datos de la base de datos de forma asíncrona, así la aplicación no se queda bloqueada mientras busca la información.
* Usuario informado: Cada acción (como publicar un alojamiento o actualizar datos) está protegida con bloques try-catch que muestran mensajes claros en pantalla si algo sale mal, evitando que la aplicación se cierre de repente.

### 4. Clase Principal (`MainApp.java`)
* Punto de Entrada: Extiende de Application de JavaFX para orquestar el ciclo de vida completo de la interfaz gráfica, asegurando una carga fluida de los archivos .fxml
* Gestión de Recursos y Ciclo de Vida: Implementa el método stop() para ejecutar JPAUtil.shutdown(), garantizando que la conexión con el servidor (AWS) se cierre de forma ordenada y segura al salir de la aplicación, evitando conexiones huérfanas.

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