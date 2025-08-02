# 🔐 REKS – Role-based Encrypted Knowledge Storage System 

A secure file management system built with **Spring Boot** that enables **encrypted file storage** and **role-based access control (RBAC)**. The system supports Admin, Manager, and Employee hierarchies, secure uploads/downloads with AES encryption, and a secure searchable interface.

## 🚀 Features

- 🔐 **AES Encryption** – All uploaded files are encrypted and securely stored.
- 👥 **Role Hierarchy** – Role-based access control:
  - Admin → Manager → Employee
- 📤 File Upload & 📥 Download
- 🔎 **Secure Search** – Index-based search over encrypted data.
- 📊 Dashboard view for recent uploads and role access.
- 🛡️ **Spring Security** integration for login & role authentication.
- 🗂️ **Thymeleaf**-based UI with Bootstrap styling.

---

## 🧑‍💻 Tech Stack

| Layer         | Technology                             |
|---------------|-----------------------------------------|
| Backend       | Spring Boot, Spring MVC, Spring Security |
| Database      | MySQL, Spring Data JPA                  |
| Encryption    | Java AES (Advanced Encryption Standard) |
| Frontend      | Thymeleaf, HTML, CSS, Bootstrap         |
| Tools         | Maven, Git, Eclipse          |

---

## 📸 Screenshots

### 🔐 Login Page
![Login Page](https://github.com/MahiSrk/role-based-encrypted-storage/blob/master/Screenshots/login.png)

### 📝 Register Page
![Register Page](https://github.com/MahiSrk/role-based-encrypted-storage/blob/master/Screenshots/register.png)

### 📂 Files Page
![Files Page](https://github.com/MahiSrk/role-based-encrypted-storage/blob/master/Screenshots/files.png)

### 📊 Dashboard
![Dashboard](https://github.com/MahiSrk/role-based-encrypted-storage/blob/master/Screenshots/dashboard.png)

### 🔍 Search Page
![Search Page](https://github.com/MahiSrk/role-based-encrypted-storage/blob/master/Screenshots/search.png)


## 🛠️ How to Run

1. **Clone the repository**

   ```bash
   git clone https://github.com/MahiSrk/role-based-encrypted-storage.git
   cd role-based-encrypted-storage
2. **Configure MySQL**
      
      Create a database reks_db
      Update your application.properties:
      
      spring.datasource.url=jdbc:mysql://localhost:3306/reks_db
   
      spring.datasource.username=root
   
      spring.datasource.password=yourpassword

4. **Run the application**

      mvn spring-boot:run

5. **Visit**
     http://localhost:8080/login

## 🚀 Future Enhancements

- 📩 **Email Notifications:** Send email alerts for file uploads, downloads, and access changes.
- 📦 **Cloud Storage Integration:** Allow encrypted file uploads to services like AWS S3, Google Cloud Storage, etc.
- 📊 **Admin Dashboard Enhancements:** Show system usage analytics, user activity graphs, and file statistics.
- 🔍 **Advanced Search:** Include filters like upload date, file type, and access role.
- 🧑‍💼 **User Self-Registration with Approval:** Allow managers and employees to request access and get approved by admin.
- 📱 **Mobile Responsive Design:** Optimize UI for mobile devices using responsive web design techniques.


