# 🤖 Plan de Desarrollo: Bot Moderador de Airsoft para Telegram

Este plan detalla las tareas, el esfuerzo estimado y los costes asociados para desarrollar un bot que automatice la moderación de anuncios de compraventa en un grupo de Telegram.

## 🎯 Resumen del Proyecto

| Característica | Detalle |
| :--- | :--- |
| **Objetivo** | Crear un bot que valide el formato de los nuevos posts (1 foto, precio, dirección, descripción) y aplique sanciones automáticas (eliminación de post y baneo temporal). |
| **Tecnología Principal** | Kotlin, Telegram Bot API. |
| **Base de Datos** | Necesaria para persistir la lógica de 'strikes' y baneos. |
| **Esfuerzo Estimado** | **11 - 12 días** de trabajo efectivo. |

---

## 🛠️ Fases y Tareas de Desarrollo

Las estimaciones de esfuerzo se basan en jornadas de 8 horas efectivas.

### Fase 1: Configuración Inicial y Conexión

| Tarea | Descripción | Esfuerzo Estimado |
| :--- | :--- | :--- |
| **1.1. Crear el Bot en Telegram** | Obtener el *token* de autenticación a través de `@BotFather`. | 1 hora |
| **1.2. Configuración del Proyecto Kotlin** | Configurar Gradle/Maven y añadir la librería de Telegram (ej: `kotlin-telegram-bot`). | 3 horas |
| **1.3. Conexión Básica (Polling)** | Implementar el bucle de *polling* para recibir actualizaciones y probar un comando simple (`/start`). | 4 horas |
| **1.4. Integración con Base de Datos** | Configurar el *driver* y definir el esquema inicial para la tabla `Sanciones` (user_id, strikes, fecha_expiracion). | 8 horas |
| **TOTAL FASE 1** | | **2 días** |

### Fase 2: Lógica de Validación de Posts

| Tarea | Descripción | Esfuerzo Estimado |
| :--- | :--- | :--- |
| **2.1. Escuchar Nuevos Mensajes** | Configurar el *handler* para procesar todos los mensajes entrantes en el grupo. | 4 horas |
| **2.2. Validar Existencia de Foto** | Verificar que el mensaje contiene un objeto `PhotoSize` (solo 1 foto). | 4 horas |
| **2.3. Validación de Contenido de Texto** | Implementar expresiones regulares (RegEx) y lógica de validación para el formato específico: | 24 horas |
| | - Búsqueda de patrón de **Precio** (ej: `[0-9]+€`). | |
| | - Búsqueda de palabras clave de **Dirección/Ubicación**. | |
| | - Verificación de longitud mínima de la **Descripción**. | |
| **2.4. Manejo de Errores Detallado** | Mapear cada fallo de validación a un mensaje de error específico (ej: "Falta precio"). | 6 horas |
| **TOTAL FASE 2** | | **4.25 días** |

### Fase 3: Lógica de Sanciones y Moderación

| Tarea | Descripción | Esfuerzo Estimado |
| :--- | :--- | :--- |
| **3.1. Implementar la API de Moderación** | Creación de funciones para: `deleteMessage` y `banChatMember`. | 10 horas |
| **3.2. Lógica de Varemos de Sanciones** | Consultar la DB, aplicar la lógica de *strikes* y calcular el tiempo de baneo: | 14 horas |
| | - **Strike 1:** Eliminación del post + 1 día de baneo. | |
| | - **Strike 2:** Eliminación del post + 7 días de baneo. | |
| | - **Strike 3:** Baneo permanente (o 30 días). | |
| **3.3. Notificación al Usuario Sancionado** | Enviar un mensaje de notificación (al grupo o en privado) indicando el motivo de la eliminación y la duración de la sanción. | 4 horas |
| **TOTAL FASE 3** | | **3.5 días** |

### Fase 4: Despliegue y Mantenimiento

| Tarea | Descripción | Esfuerzo Estimado |
| :--- | :--- | :--- |
| **4.1. Configuración de Alojamiento** | Preparar el entorno de ejecución (Docker, JAR) en el servicio de hosting elegido (VPS, Railway, Heroku). | 8 horas |
| **4.2. Pruebas End-to-End (QA)** | Pruebas exhaustivas de todos los flujos (posts correctos, fallos, repetición de fallos, desbaneo). | 6 horas |
| **4.3. Script de Mantenimiento** | Lógica para limpiar la DB o revisar el estado de los baneos expirados (si es necesario). | 2 horas |
| **TOTAL FASE 4** | | **2 días** |

---

## ☁️ Contexto Tecnológico y Coste Estimado

### Servicios Requeridos

1.  **Telegram Bot API:** El servicio de mensajería.
2.  **Lenguaje y Framework:** Kotlin y una librería de *wrappers* (ej: `kotlin-telegram-bot`).
3.  **Base de Datos:** PostgreSQL o SQLite (si se autogestiona en el servidor).
4.  **Alojamiento (Hosting):** Un servidor que mantenga la aplicación Kotlin ejecutándose 24/7.

### 💰 Coste Estimado Mensual (Hosting y DB)

El servicio de bot es gratuito. Los costes se centran en el alojamiento y la base de datos.

| Servicio | Opción Recomendada (Inicial) | Coste Estimado Mensual | Notas |
| :--- | :--- | :--- | :--- |
| **Alojamiento (Hosting)** | VPS Básico (ej: DigitalOcean, Linode) o PaaS (*Starter*) como Railway/Heroku. | **€5 - €15** | Suficiente para un tráfico moderado de mensajes. |
| **Base de Datos (DB)** | PostgreSQL instalado en el mismo VPS o un *tier* gratuito/mínimo de un servicio gestionado (ej: MongoDB Atlas, AWS RDS). | **€0 - €10** | El coste es cero si usas el mismo servidor del *hosting* o un *tier* gratuito. |
| **Telegram API** | Uso de la API estándar. | **€0** | Es un servicio gratuito. |
| **TOTAL ESTIMADO** | | **€5 - €25 / mes** | El coste puede variar dependiendo del proveedor y la región. |

***

## 💡 Recomendaciones para el Backend Developer

1.  **Elige Polling sobre Webhook:** Para tu primer bot y un solo grupo, el método de **Long Polling** (tu bot pide actualizaciones a Telegram) es mucho más sencillo de configurar que un **Webhook** (Telegram avisa a tu servidor), que requiere un *endpoint* HTTPS expuesto.
2.  **Expresiones Regulares (RegEx):** Invierte tiempo en las RegEx de la Fase 2.3. Unas RegEx robustas son clave para garantizar que el formato de los anuncios sea estricto. Por ejemplo, podrías obligar a que el precio y la dirección estén precedidos por una etiqueta específica (ej: `[PRECIO: 150€]`, `[UBICACION: Madrid]`).
3.  **Permisos del Bot:** Asegúrate de que el bot sea **Administrador** en el grupo con los permisos necesarios para **"Eliminar mensajes"** y **"Restringir usuarios"**.
