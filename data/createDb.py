import pandas as pd
import sqlite3


excel_file = 'medicaments.xlsx'
sheet_name = 'med'            

data = pd.read_excel(excel_file, sheet_name=sheet_name)

db_name = 'med_guide.db' 
conn = sqlite3.connect(db_name)

table_name = 'medicaments'  
data.to_sql(table_name, conn, if_exists='replace', index=False)

print(f"Data has been successfully imported into the '{table_name}' table in '{db_name}'.")

cursor = conn.cursor()
cursor.execute(f"SELECT * FROM {table_name}")
rows = cursor.fetchall()

print("Sample data from the table:")
for row in rows[:5]:  
    print(row)

conn.close()
