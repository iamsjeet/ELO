# ELO
ELO based dynamic recomendation system for shoping cart

1. Project Title
ELO Rating & Recommendation System

2. Project Description
The ELO Rating & Recommendation System is a full-stack Java and Web application that utilizes the ELO algorithm to dynamically rank products based on real user preferences.

Users engage in head-to-head product comparisons, allowing the system to continuously update both global and personalized ELO scores. The core logic—including the ELO engine, data persistence, and recommendation services—is built in Java, which serves a modern HTML/CSS/JavaScript frontend over a local HTTP server.

To provide intelligent and personalized suggestions, the application also integrates Content-Based Filtering and Collaborative Filtering.

3. Purpose of the Project
This project addresses the limitations of static star ratings by implementing a more reliable, competition-style scoring model.

The system is designed to:
- Apply the mathematically sound ELO rating model to everyday product comparisons
- Maintain a dual-layer rating system (global + personalized)
- Combine ELO ranking with Content-Based and Collaborative Filtering
- Use flat-file (.txt) persistence without requiring a database
- Demonstrate how recommendation systems can be built from scratch

4. Steps to Run the Code

Prerequisites:
- Java JDK (Version 11 or later)
- A modern web browser (Chrome / Firefox / Edge)

Execution Steps:

Step 1: Compile the Java Source Files
javac -d out src/**/*.java Main.java ConsoleMain.java

(If glob patterns are not supported)
javac -d out Main.java ConsoleMain.java src/model/*.java src/engine/*.java src/service/*.java src/persistence/*.java src/comparison/*.java src/api/*.java

Step 2: Verify Data Files
Ensure the following files are present:
- global_ratings.txt
- user_ratings.txt

If missing, ratings initialize to 1000.0 (default ELO value).

Step 3: Start the HTTP Server
java -cp out Main

Server runs on:
http://localhost:8080

Step 4: Launch the Web Application
Open: login.html

Step 5: Terminal Mode (Optional)
java -cp out ConsoleMain

5. Required Inputs & Expected Outputs

Inputs:
- User Selection: Select a predefined user (e.g., AlexR, Megan)
- Product Choice: Click “Choose This” on a product card
- Category Filter: Filter products (Books, Electronics, Fashion)
- Search Query: Search products by name or category
- global_ratings.txt: Stores productId,rating pairs
- user_ratings.txt: Stores userName,productId,rating values

Expected Outputs:
- Updated Product Cards: UI refresh with updated ELO scores
- Toast Notification: “ELO ratings updated!”
- global_ratings.txt: Updated global ELO scores
- user_ratings.txt: Updated personalized scores
- log.txt: Stores comparison logs
- Console Output: Ranked results (ConsoleMain)

6. Individual Contribution of the Student

Shreejeet Gupta:
- Designed and implemented complete system architecture
- Developed core backend logic (ELO Ranking, Personalized ELO, Recommendation Engine)
- Implemented data persistence and logging system
- Built full frontend functionality and integration
- Created test cases and validation logic

© 2025 ELO Rating System — Trust what real people rate.

