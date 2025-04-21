# Xpay - Payment Gateway and Wallet System

Xpay is an MVC-based B2B Payment Gateway and Wallet System built using Spring Boot. It is designed to seamlessly integrate with any business application and supports multiple clients. Each client can manage their own set of users and handle wallet operations securely.

## Features

- **Multi-client Support**: Allows each client to manage their own users and wallets.
- **User Management**: Clients can create and manage their users' data.
- **Wallet Creation**: Users can create wallets through their respective clients.
- **Secure Transactions**: Ensures secure and reliable transactions.
- **JWT-based Authentication**: Secure user authentication using JSON Web Tokens (JWT).
- **OAuth for Clients and Admins**: OAuth-based authentication for clients and admins.
- **Client Signup and Approval**: Clients can sign up, and admins can approve or reject their requests.
- **Client-Specific Wallet Usage**: Wallets can only be used within the same client’s services.
- **Extensible**: Designed to be flexible and extensible to fit the needs of any business application.

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6.0 or higher

### Installation

1. **Clone the Repository:**

    ```bash
    git clone https://github.com/mrsharukh03/Xpay.git
    cd Xpay
    ```

2. **Build the Project:**

    ```bash
    mvn clean install
    ```

3. **Run the Application:**

    ```bash
    mvn spring-boot:run
    ```

### Configuration

Update the `application.properties` file with your database configuration and other necessary settings.

```properties
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE}
  server:
    address: ${SPRING_SERVER_ADDRESS}
    port: ${SPRING_SERVER_PORT}
    ssl:
      key-store: ${SPRING_SSL_KEY_STORE}
      key-store-password: ${SPRING_SSL_KEY_STORE_PASSWORD}
      key-store-type: ${SPRING_SSL_KEY_STORE_TYPE}
      key-alias: ${SPRING_SSL_KEY_ALIAS}

  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: ${SPRING_DATASOURCE_DRIVER_CLASS_NAME}

  jpa:
    hibernate:
      ddl-auto: ${SPRING_JPA_HIBERNATE_DDL_AUTO}
    database-platform: ${SPRING_JPA_DATABASE_PLATFORM}

  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${OAUTH2_GOOGLE_CLIENT_ID}
            client-secret: ${OAUTH2_GOOGLE_CLIENT_SECRET}
            scope: ${OAUTH2_GOOGLE_SCOPE}
        provider:
          google:
            authorization-uri: ${OAUTH2_GOOGLE_AUTHORIZATION_URI}
            token-uri: ${OAUTH2_GOOGLE_TOKEN_URI}
            user-info-uri: ${OAUTH2_GOOGLE_USER_INFO_URI}
            user-name-attribute: ${OAUTH2_GOOGLE_USER_NAME_ATTRIBUTE}

  mail:
    host: ${MAIL_HOST}
    port: ${MAIL_PORT}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          timeout: 5000
          connectiontimeout: 5000

jwt:
  secret-key: ${JWT_SECRET_KEY}
```

### Usage

1. **Register a Client**: Clients need to register to obtain their credentials.
2. **Create Users**: Clients can create and manage users.
3. **Create Wallets**: Users can create wallets through their respective clients.
4. **Process Payments**: Utilize the provided APIs to process payments securely.
5. **Admin Actions**: Admins can approve or reject client signup requests and manage other operations.

## API Documentation

The API documentation is available at `/swagger-ui.html` once the application is running.

### Operations

#### Wallet Operations

- **POST** `/api/v1/wallet/payFor` - Process a payment.
- **POST** `/api/v1/wallet/deposit` - Deposit funds into a wallet.
- **POST** `/api/v1/wallet/create-wallet` - Create a new wallet.
- **GET** `/api/v1/wallet/transactions` - Get a list of wallet transactions.
- **GET** `/api/v1/wallet/transaction` - Get details of a specific transaction.
- **GET** `/api/v1/wallet/balance` - Get wallet balance.

#### Client Operations

- **POST** `/api/v1/client/auth/verify-otp` - Verify OTP for client authentication.
- **POST** `/api/v1/client/auth/register` - Client registration.
- **POST** `/api/v1/client/auth/login` - Client login.
- **POST** `/api/v1/client/auth/forget-password` - Request a password reset.
- **PATCH** `/api/v1/client/verifyUser` - Verify user for a client.
- **PATCH** `/api/v1/client/refund-money` - Process a refund.
- **GET** `/api/v1/client/transactions` - Get client transactions.
- **GET** `/api/v1/client/balance` - Get client balance.

#### Admin Operations

- **POST** `/api/v1/admin/auth/signup` - Admin signup.
- **POST** `/api/v1/admin/auth/resendOTP` - Resend OTP to admin.
- **POST** `/api/v1/admin/auth/otp-verify` - Verify OTP for admin authentication.
- **POST** `/api/v1/admin/auth/newPassword` - Set a new password for the admin.
- **PATCH** `/api/v1/admin/blockClient` - Block a client.
- **PATCH** `/api/v1/admin/approve-client` - Approve a client registration.
- **PATCH** `/api/v1/admin/approve-admin` - Approve an admin registration.
- **GET** `/api/v1/admin/users` - List of all users.
- **GET** `/api/v1/admin/pending` - List of pending client registrations.
- **GET** `/api/v1/admin/clients` - List of all clients.
- **GET** `/api/v1/admin/admins` - List of all admins.

#### User Operations

- **PATCH** `/api/v1/user/updateProfile` - Update user profile.
- **GET** `/api/v1/user/login` - User login.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact

For any inquiries or support, please contact [mrsharukh03](https://github.com/mrsharukh03).

---
