# Yapily | e-commerce API Exercise

Imagine an e-commerce shop, for example Etsy. We would like you to build an API that provides access to
the shop's capabilities.

There are two parts to this exercise:
- **managing store products** - creating/deleting inventory, listing existing products, etc.
- **managing shopping carts** - creating new shopping carts, adding products, checking out, etc.

The idea is to implement the endpoints described in this document, add tests for these endpoints which
should cover common usage scenarios and edge-cases.

# Solution

### Tech Stack
| Technology                    | Version           |
|-------------------------------|-------------------|
| **Java**                      | 17.0.1 2022-10-19 |
| **Spring Boot**               | 3.2.2             |
| **Spring JPA**                | 3.2.2             |
| **PostgreSQL**                | 14.6.0            |
| **H2 Memory**                 | 2.1.212           |
| **Project Lombok**            | 1.18.30           |
| **Jupiter JUnit**             | 3.2.2             |
| **Mockito**                   | 4.8.1             |
| **Springdoc OpenAPI Swagger** | 2.0.2             |

## Approach
The idea is to show knowledge to solve the problem but at the same time maintain simplicity as it is a technical exercise.

In my understanding, we have the situation of a shopping cart, where we can have a cart with `N` products inside and on the other hand, a product, when registered, can be inserted into `N` carts.  
For this reason, I chose to use object-relational mapping `@ManyToMany` where a cart has a list of products and a product has a list of carts.

Using `JPA/Hibernate` as ORM, in this context we necessarily have a third table that relates carts to products and vice versa.  
So our database is structured in the following way:

Table `cart`:
- `id`
- `check_out`
- `total_cost`

Table `product`:
- `id`
- `name`
- `price`
- `labels`
- `added_at`

Table `cart_product`:
- `cart_id`
- `product_id`

### Configuring the Environment to Run the Application

You can find a `docker-compose.yml` file in the project root for this solution to work.  
Run the following command:
```bash
docker-compose up -d
```

This will prepare and run a docker instance with a PostgreSQL database running so that the application works correctly.

### Great! We are ready to run our application 💪

**IDE (IntelliJ, Eclipse, Netbeans, etc):**
- Importing the project as a Maven project on your IDE.
- Build the project using Java 17
- Run/Debug project from Main Application Class :: `co.uk.yapily.YapilyECommerceApiApplication.java`

**Terminal:**
```bash 
./mvnw spring-boot:run
```

### Running the Tests
**Terminal:**
```bash 
./mvnw test
```

### Accessing Database:
**PostgreSQL:**
```sql
jdbc:postgresql://localhost:5432/postgres
user: postgres
pass: postgres
```
**H2 Memory:**
```sql
jdbc:h2:mem:yapily;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;
user: sa
pass: 1234
```

## API documentation
- http://localhost:8080/swagger-ui.html

## Docker
A `Dockerfile` is prepared to download an OpenJDK 17 Slim and install the application.  
Run the following command:

```dockerfile
docker build --no-cache -t yapily .
docker run -p 8080:8080 yapily
```

**Important point:**

Since we have our application running in a docker `container A` and our PostgreSQL database running in `container B`, they do not see each other directly.  
To do this, they must be on the same network and dependent on each other.

As this is just an exercise that will be run locally, I left a `docker-compose-production.yml` in case they wanted to do the tests via Docker.

## APIs:
The basic URL paths are: 
- http://localhost:8080/products
- http://localhost:8080/carts

#### Endpoints:
- List all products - GET /products
- List one product - GET /products/:id
- Create a new product - POST /products
- Delete an existing product - DELETE /products/:id  


- Create a shopping cart - POST /carts
- List all shopping carts - GET /carts
- Modify a shopping cart - PUT /carts/:id
- Checkout a shopping cart - POST /carts/:id/checkout