# Memorama (Clean Architecture · Navigation Controller · Conexión con Drive · View Binding · Persistencia de datos)

**Memorama** es una aplicación construida con **Kotlin**, pensada como una base limpia, escalable y profesional para el desarrollo de proyectos.
El objetivo es crear una aplicación donde se pueda jugar al Memorama, un juego famoso en el cual se deben voltear cartas con el objetivo de encontrar la pareja de todas las cartas dentro del tablero. En esta aplicación se incluyen diferentes funcionalidades, como la de empezar intentos donde se pueden escoger distintas configuraciones (por ejemplo, tamaño del tablero y tipo de cartas). Otra funcionalidad es la de mostrar la solución, donde se voltean todas las cartas, además de realizar una restauración o respaldo en la cuenta de Drive del usuario o, en su defecto, consultar los intentos realizados por el usuario almacenados en el dispositivo con SQLite, entre otras.

Demo de la app:

* [demo]()

---

## Tech Stack 🚀

* **Lenguaje:** Kotlin
* **Arquitectura:** Clean Architecture
* **Reactivo:** SQLite para almacenar intentos
* **Testing:** Por implementar
* **Inyección de dependencias:** Posible mejora para peticiones

---

## Features 🧩

* Jugar al memorama.
* Modificar la configuración del juego.
* Mostrar la solución del intento actual.
* Revisar intentos realizados por el usuario.
* Terminar intento desde el menú.
* Conexión con Drive para crear respaldo de intentos.
* Navigation Controller y View Binding para utilizar mejores prácticas en el desarrollo de proyectos.

---

## Qué se busca en esta aplicación ✅

* **Separación clara de pantallas:** Cada pantalla es responsable de su lógica y se encuentra correctamente organizada.
* **DI-friendly design** — fácil de sustituir mocks por implementaciones reales en CI.
* **Base escalable** — preparada para autenticación, caché, sincronización y funcionalidades avanzadas.

---

## Estructura del proyecto (resumen) 🗂

ETSMemorama/
├── data/db # Persistencia de datos SQLite
├── domain/model # Modelo de carta
├── ui/ # Pantallas y lógica
└── MainActivity # Contenedor principal

---

## Cómo ejecutar 🛠️

* Clonar el proyecto.
* Ejecutar con Android Studio.
* Para correr: `Control + R` o presionar el botón de *play*.

---

## Autor ✒️

* Ian Axel de la Torre — Desarrollo completo — [IandelaTorre](https://github.com/IandelaTorre).

---

## Próximos pasos / mejoras 📈

* Integrar peticiones API REST.
* Añadir autenticación (Coordinator-friendly login flow).
* Mejorar la interfaz: animaciones, indicadores, etc.
* Incluir tests unitarios dentro del proyecto.
* Implementar CI con GitHub Actions para ejecutar tests en cada PR.

---
