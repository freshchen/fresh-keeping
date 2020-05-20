import argparse
import xlrd
import os


def test(path):
    data = xlrd.open_workbook(path)
    table = data.sheets()[0]

    temp = table.row_values(0)[0]

    table_name = re.findall("[A-Z].*\w+", temp)[0]

    nrows = table.nrows
    sql = "DROP TABLE "
    sql = "create table " + table_name + "( \n"
    for rownum in range(2, nrows):
        row = table.row_values(rownum)

        if row and rownum != (nrows - 1):

            if row[1] == "ID":
                temp = float(row[3])
                sql += row[1] + " " + row[2] + "(" + str(int(temp)) + ") " + "PRIMARY KEY,\n"
            else:
                sql += row[1] + " "
                if re.search("DECI.*", row[2]):
                    sql += " " + row[2]
                elif row[2] == "NUMBER" and row[3] == 8:
                    sql += " int "
                elif row[2] == "NUMBER" and row[3] == 1:
                    sql += " smallint "
                elif row[2] == "NUMBER" and row[3] > 10:
                    sql += "bigint"
                elif row[2] == "DATETIME":
                    sql += " timestamp "
                elif row[2] == "DATE":
                    sql += " date "
                else:
                    temp = float(row[3])
                    sql += " " + row[2] + "(" + str(int(temp)) + ") "

                if row[4] == "Y" and row[5] == "Y":
                    sql += " NOT NULL UNIQUE,\n"

                elif row[4] == "Y" and row[5] != "Y":
                    sql += " NOT NULL,\n"
                elif row[4] != "Y" and row[5] != "Y":
                    sql += ",\n"
        else:
            sql += row[1] + " "

            if re.search("DECI.*", row[2]):
                sql += " " + row[2]
            else:
                temp = float(row[3])
                sql += " " + row[2] + "(" + str(int(temp)) + ") "

            if row[4] == "Y" and row[5] == "Y":
                sql += " NOT NULL UNIQUE,\n"

            elif row[4] == "Y" and row[5] != "Y":
                sql += " NOT NULL,\n"
            elif row[4] != "Y" and row[5] != "Y":
                sql += " \n)"

    print(sql)


def parse(path):
    (filepath, tempfilepager) = os.path.split(path)
    (table_name, extension) = os.path.splitext(tempfilepager)
    data = xlrd.open_workbook(path)
    print(table_name)


def main():
    path_list = [
        "/Users/chenling/Documents/work/sanyi/TB_MZ_JSZFFSMXB.xlsx"
    ]
    for path in path_list:
        parse(path)


if __name__ == '__main__':
    main()
