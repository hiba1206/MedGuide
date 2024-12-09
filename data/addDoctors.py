import pandas as pd
import sqlite3

file_path = 'doctors.xlsx'  
data = pd.read_excel(file_path, engine='openpyxl')  

db_path = '../app/src/main/assets/med_guide.db'  
conn = sqlite3.connect(db_path)
cursor = conn.cursor()

table_name = 'doctors' 
data.to_sql(table_name, conn, if_exists='replace', index=False)

cursor.execute(f"SELECT * FROM {table_name} LIMIT 5;")
for row in cursor.fetchall():
    print(row)

conn.close()
