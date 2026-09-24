# Customers Backend Challenge

Microservicio **Customers** desarrollado con Spring Boot para la gestión de clientes.

El microservicio es responsable de la creación y consulta de clientes, así como del cálculo de indicadores relacionados con sus fechas de nacimiento.

La aplicación expone una API REST que permite:

- Crear clientes.
- Consultar clientes sin filtros.
- Consultar clientes por DNI o email.
- Obtener indicadores de natalidad por mes/año.

## Tecnologías

- Java 17
- Spring Boot
- Maven
- Spring Web
- Spring Data JPA
- Bean Validation
- H2 Database
- JUnit 5
- Mockito

## Requisitos

Para ejecutar el proyecto es necesario tener instalado:

- Java 17 o superior

No es necesario instalar Maven, ya que el proyecto incluye **Maven Wrapper**.

## Instalación

Clonar el repositorio:

```bash
git clone https://github.com/gngian10/customers-backend-challenge.git
cd customers-backend-challenge
```

## Ejecución

### Windows

```bash
.\mvnw.cmd spring-boot:run
```

### Linux / macOS

```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en:

```text
http://localhost:8080
```

## CORS

El backend está configurado para permitir solicitudes desde el frontend de la aplicación Customers.

Durante la ejecución local, el frontend Angular utiliza:

```text
http://localhost:4200
```

Esto permite que el frontend pueda consumir la API REST expuesta por el microservicio en:

```text
http://localhost:8080
```

## Base de datos

El proyecto utiliza H2 en memoria para facilitar la ejecución local y la evaluación técnica.

Configuración principal:

- **JDBC URL:** `jdbc:h2:mem:customersdb`
- **User:** `sa`
- **Password:** *(vacío)*

La consola H2 está disponible en:

```text
http://localhost:8080/h2-console
```

Al utilizar una base de datos en memoria, los datos se eliminan cuando la aplicación se detiene o reinicia.

## Modelo de Cliente

Un cliente contiene los siguientes campos:

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | Identificador generado automáticamente |
| `nombre` | String | Nombre del cliente |
| `apellido` | String | Apellido del cliente |
| `email` | String | Email único |
| `dni` | String | DNI único de 8 dígitos |
| `fechaCreacion` | LocalDateTime | Fecha generada automáticamente por el backend |
| `fechaNacimiento` | LocalDate | Fecha de nacimiento |

> `fechaCreacion` no se recibe desde el cliente. Es generada automáticamente por el backend al registrar un nuevo cliente.

## API

**Base URL:**

```text
http://localhost:8080/api/customers
```

### Crear cliente

```text
POST /api/customers
```

Ejemplo:

```json
{
  "nombre": "John",
  "apellido": "Doe",
  "email": "john.doe@email.com",
  "dni": "12345678",
  "fechaNacimiento": "1995-04-15"
}
```

Respuesta exitosa:

```text
201 Created
```

Ejemplo de respuesta:

```json
{
  "id": 1,
  "nombre": "John",
  "apellido": "Doe",
  "email": "john.doe@email.com",
  "dni": "12345678",
  "fechaCreacion": "2026-09-10T17:23:22",
  "fechaNacimiento": "1995-04-15"
}
```

### Consultar todos los clientes

```text
GET /api/customers
```

Retorna todos los clientes registrados.

### Consultar por DNI

```text
GET /api/customers?dni=12345678
```

### Consultar por email

```text
GET /api/customers?email=john.doe@email.com
```

### Consultar por DNI y email

```text
GET /api/customers?dni=12345678&email=john.doe@email.com
```

Los filtros son opcionales.

Cuando una búsqueda no encuentra resultados, se retorna:

```json
[]
```

con estado:

```text
200 OK
```

## Indicadores

```text
GET /api/customers/indicadores
```

El endpoint retorna:

- Cantidad de clientes nacidos por mes/año.
- Mes/año con mayor cantidad de clientes nacidos.
- Mes/año con menor cantidad de clientes nacidos.
- Tasa de natalidad de cada mes/año.

Ejemplo:

```json
{
  "natalidadPorMesAnio": [
    {
      "mes": 4,
      "anio": 1995,
      "cantidad": 2,
      "tasaNatalidad": 50.0
    },
    {
      "mes": 8,
      "anio": 1995,
      "cantidad": 1,
      "tasaNatalidad": 25.0
    },
    {
      "mes": 2,
      "anio": 2000,
      "cantidad": 1,
      "tasaNatalidad": 25.0
    }
  ],
  "mesAnioConMayorNatalidad": {
    "mes": 4,
    "anio": 1995,
    "cantidad": 2,
    "tasaNatalidad": 50.0
  },
  "mesAnioConMenorNatalidad": {
    "mes": 8,
    "anio": 1995,
    "cantidad": 1,
    "tasaNatalidad": 25.0
  }
}
```

### Cálculo de tasa de natalidad

Para esta implementación, la tasa de natalidad representa el porcentaje de clientes registrados que nacieron en un determinado mes/año respecto al total de clientes registrados.

La fórmula utilizada es:

```text
(cantidad de clientes nacidos en el mes/año / total de clientes registrados) × 100
```

Por ejemplo, si existen cuatro clientes registrados y dos de ellos nacieron en abril de 1995:

```text
(2 / 4) × 100 = 50 %
```

El resultado se expresa como porcentaje y se redondea a dos decimales.

En caso de empate para el mayor o menor número de nacimientos, se retorna el primer mes/año encontrado según el orden utilizado por la aplicación.

## Validaciones

Durante la creación de clientes se validan las siguientes reglas:

- Nombre obligatorio.
- Apellido obligatorio.
- Email obligatorio y con formato válido.
- DNI obligatorio y compuesto por exactamente 8 dígitos.
- Fecha de nacimiento obligatoria.
- La fecha de nacimiento no puede ser futura.
- DNI único.
- Email único.

### Error de validación

Las validaciones de entrada retornan:

```text
400 Bad Request
```

Ejemplo:

```json
{
  "status": 400,
  "message": "Error de validación",
  "errors": {
    "email": "El email debe tener un formato válido",
    "dni": "El DNI debe contener exactamente 8 dígitos"
  },
  "timestamp": "2026-09-10T17:30:00"
}
```

### Cliente duplicado

Un DNI o email ya registrado retorna:

```text
409 Conflict
```

Ejemplo:

```json
{
  "status": 409,
  "message": "Ya existe un cliente con el DNI: 12345678",
  "timestamp": "2026-09-10T17:30:00"
}
```

## Arquitectura

El proyecto utiliza una arquitectura por capas:

```text
src/main/java/com/customers
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── service
└── CustomersBackendChallengeApplication.java
```

Responsabilidades principales:

- **controller:** exposición de la API REST.
- **service:** lógica de negocio.
- **repository:** acceso a datos mediante Spring Data JPA.
- **entity:** modelo de persistencia.
- **dto:** contratos de entrada y salida de la API.
- **exception:** manejo centralizado de errores.

Se eligió una arquitectura en capas debido al alcance acotado del microservicio, evitando introducir complejidad innecesaria.

## Tests

Los tests unitarios utilizan JUnit 5 y Mockito.

Actualmente se cubren comportamientos críticos del servicio:

- Creación exitosa de clientes.
- Rechazo de clientes con DNI duplicado.
- Cálculo de indicadores por mes/año.

Para ejecutar los tests:

### Windows

```bash
.\mvnw.cmd test
```

### Linux / macOS

```bash
./mvnw test
```

## Postman

La colección de Postman se encuentra en:

```text
postman/customers-api.postman_collection.json
```

La colección incluye requests para:

- Crear clientes.
- Consultar todos los clientes.
- Consultar por DNI.
- Consultar por email.
- Consultar por DNI y email.
- Consultar indicadores.

La variable `baseUrl` tiene como valor por defecto:

```text
http://localhost:8080
```

La colección puede importarse directamente desde Postman utilizando la opción **Import**.
