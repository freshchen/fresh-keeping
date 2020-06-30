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
        SQL += "update zf_customer set qie_id=" + str(row[1]) + " where id=" + str(row[0]) + ";\n"


def main():
    path_list = [
        "./1.csv"
    ]

    for path in path_list:
        data = pd.read_csv(path)
        parse(data)

    write_file(SQL, "zf_customer-update.sql")


if __name__ == '__main__':
    main()
