import os
import pandas as pd

SQL = ""


def write_file(s, file_name):
    fh = open(file_name, 'w', encoding='utf-8')
    fh.write(s)
    fh.close()


def parse(data):
    global SQL
    for row in data.values:
        SQL += "INSERT IGNORE INTO zfhis.source_code_mapping (source_id, biz_code) VALUES ('" + str(row[0]) + "', '" + str(row[1]) + "');\n"


def main():
    path_list = [
        "./source.xlsx"
    ]

    for path in path_list:
        data = pd.read_excel(path)
        parse(data)

    write_file(SQL, "source-mapping.sql")


if __name__ == '__main__':
    main()
