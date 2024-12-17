# Bookstore Application

Welcome to the **Bookstore Application**! This is a full-stack web application that allows users to browse, search, filter, and purchase books. Administrators can manage the platform's content, while regular users can maintain accounts, manage their orders, and check out using an integrated basket system.

---

## Features

### User Features
- **Book Browsing**: View available books with detailed information.
- **Search and Filter**: Search by title or author and filter books by price range.
- **Basket Management**: Add or remove books from the basket and view the total price.
- **Order History**: View detailed purchase history, including order date, books purchased, and total cost.
- **Account Management**: Create an account, update account details, and delete accounts securely.

### Admin Features
- **Book Management**: Add, update, and delete books from the database.
- **Order Management**: View all user orders.
- **User Management**: View user accounts and roles.

---

## Technologies Used

### Backend
- **Spring Boot**: Framework for creating RESTful APIs and handling server-side logic.
- **PostgreSQL**: Relational database for storing book, user, and order information.
- **JPA/Hibernate**: ORM for managing database interactions.
- **Spring Security**: Provides user authentication and authorization.
- **Thymeleaf**: Server-side rendering engine for generating HTML views.

### Frontend
- **HTML/CSS**: Styling and layout of the pages.
- **Thymeleaf**: Dynamic rendering of content.

### Others
- **AWS S3**: For hosting images associated with books (optional).


---

## Setup Instructions

### Prerequisites
1. Install Java 21 or later.
2. Install PostgreSQL and ensure it is running.
3. Set up an IDE (e.g., IntelliJ IDEA, Eclipse) for development.
4. Clone this repository using:
   ```bash
   git@github.com:viktoriiashyshkina/Bookstore.git

### Backend Configuration

1. **Create a PostgreSQL Database**  
   Open your PostgreSQL client and execute the following SQL command to create the database:
   ```sql
   CREATE DATABASE bookstore;
    
2. Update the `application.properties` File

Configure the database connection and other settings in `src/main/resources/application.properties`. Here's an example configuration:

```properties
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/bookstore
   spring.datasource.username=your-username
   spring.datasource.password=your-password

   # JPA and Hibernate Configuration
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true

   # Server Port (optional)
   server.port=8080  
```
### Build and Run the Backend

To run the backend, use the following command:

```bash
./mvnw spring-boot:run
```
### Project Structure
```
src/
├── main/
│   ├── java/com/project/
│   │   ├── config/    # Spring Security configuration and Dynamic Scheduling Configuration 
│   │   ├── controller/      # All REST and MVC controllers
│   │   ├── entity/         # JPA entity classes
│   │   ├── exception/        # Custom exception handling classes
│   │   ├── repository/     # Spring Data JPA repositories
│   │   ├── service/         # Service layer logic
│   │   ├── util/             # Utility classes for helper functions
│   ├── resources/
│   │   ├── templates/        # Thymeleaf HTML templates
│   │   ├── static/           # Static assets (CSS, JS, images)
│   │   ├── application.properties # Configuration file
└── ...
```# Bookstore