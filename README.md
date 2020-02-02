# JavaLoginValidationForm
Console application written with the tools
in the JAVA OOP language. The program has the following functionalities:
1. User can register and login with entering username and password.
2. Using jdbc connection to sqlite database file to check the entered username and password.
3. If the username and password match user info shows from database.
4. If there is no username and password there is algorithm for
protection against brute-force attacks (blocking for a long time when multiple attempts are made to connect).
5. System Audit (table in sqlite database recording user activity).
6. Implemented log4j
7. Passwords are stored in sqlite db as a salted hash.
