import pandas as pd
import sqlite3

# File paths
medicaments_file_path = 'medicaments.xlsx'
doctors_file_path = 'doctors.xlsx'

# Read data from Excel files
medicaments_data = pd.read_excel(medicaments_file_path, sheet_name='med', engine='openpyxl')
doctors_data = pd.read_excel(doctors_file_path, engine='openpyxl')

# Database path (make sure it's the correct path for your project)
db_path = '../app/src/main/assets/med_guide.db'

# Connect to SQLite database
conn = sqlite3.connect(db_path)
cursor = conn.cursor()

# Create 'medicaments' table and insert data
medicaments_table_name = 'medicaments'
medicaments_data.to_sql(medicaments_table_name, conn, if_exists='replace', index=False)

print(f"Data has been successfully imported into the '{medicaments_table_name}' table in '{db_path}'.")

# Print sample data from 'medicaments' table
cursor.execute(f"SELECT * FROM {medicaments_table_name} LIMIT 5;")
print("Sample data from medicaments table:")
for row in cursor.fetchall():
    print(row)

# Create 'doctors' table and insert data
doctors_table_name = 'doctors'
doctors_data.to_sql(doctors_table_name, conn, if_exists='replace', index=False)

print(f"Data has been successfully imported into the '{doctors_table_name}' table in '{db_path}'.")

# Print sample data from 'doctors' table
cursor.execute(f"SELECT * FROM {doctors_table_name} LIMIT 5;")
print("Sample data from doctors table:")
for row in cursor.fetchall():
    print(row)

# Close the database connection
conn.close()
