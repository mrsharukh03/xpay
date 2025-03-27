# Xpay

Xpay is an MVC-based Payment Gateway and Wallet System built using Spring Boot. It is designed to integrate seamlessly with any business application. The system supports multiple clients, allowing them to manage their users' data and create wallets for their users.

## Features

- **Multi-client Support**: Xpay supports multiple clients, each managing their own set of users.
- **User Management**: Clients can create and manage their users' data.
- **Wallet Creation**: Users can create wallets through their respective clients.
- **Secure Transactions**: Ensures secure and reliable transactions.
- **JWT-based Authentication**: Secure authentication using JWT for users.
- **OAuth for Clients and Admins**: Clients and Admins can authenticate using OAuth.
- **Client Signup and Approval**: Clients can sign up with their details, and Admins can approve or reject their signup requests.
- **Client-specific Wallet Usage**: Wallets can only be used for transactions within the same client’s services.
- **Extensible**: Designed to be flexible and extensible to meet the needs of any business application.

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6.0 or higher

### Installation

1. **Clone the repository**:
    ```bash
    git clone https://github.com/mrsharukh03/Xpay.git
    cd Xpay
    ```

2. **Build the project**:
    ```bash
    mvn clean install
    ```

3. **Run the application**:
    ```bash
    mvn spring-boot:run
    ```

### Configuration

Update the `application.properties` file with your database configuration and other necessary settings.

### Usage

1. **Register a Client**: Clients need to register and obtain their credentials.
2. **Create Users**: Clients can create and manage their users.
3. **Create Wallets**: Users can create wallets through their clients.
4. **Process Payments**: Use the provided APIs to process payments securely.
5. **Admin Actions**: Admins can approve or reject client signup requests.

## API Documentation

The API documentation is available at `/swagger-ui.html` once the application is running.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

For any inquiries or support, please contact [mrsharukh03](https://github.com/mrsharukh03).
