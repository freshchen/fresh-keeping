import os
import xlrd

SQL = ""
TYPE_DICT = {"字符串": "VARCHAR", "字符": "CHAR", "数字": "DECIMAL", "DATE": "DATE", "Datetime": "TIMESTAMP",
             "日期时间": "TIMESTAMP", "DATETIME": "TIMESTAMP", "数值": "DECIMAL", "整数": "DECIMAL"}
NEED_DICT = {"必填": "NOT NULL", "应填": "NULL", "不填": "NULL", "": "NULL"}


def write_file(s, file_name):
    fh = open(file_name, 'w', encoding='utf-8')
    fh.write(s)
    fh.close()


def trim(s):
    return str(s).strip()


def field_length(s):
    if isinstance(s, float) or isinstance(s, int):
        (s1, s2) = str(float(s)).split(".", 1)
        if "0" == s2:
            s2 = ""
        else:
            s2 = "," + s2
        return "(" + s1 + s2 + ") "
    else:
        return " "


def parse_row(row):
    global SQL
    SQL += "  "
    SQL += "\"" + trim(row[1]) + "\" "
    SQL += TYPE_DICT[trim(row[2])]
    SQL += field_length(row[3])
    SQL += NEED_DICT[trim(row[4])]


def parse_sheet(sheet, table_name, row_begin):
    global SQL
    sheet_rows = sheet.nrows
    SQL += "CREATE TABLE \"" + table_name + "\" (\n"
    for row_num in range(row_begin, sheet_rows):
        row = sheet.row_values(row_num)
        not_empty = list(filter(lambda s: "" != s, [row[1], row[2], row[3], row[4]]))
        if row and len(not_empty) and row_num != (sheet_rows - 1):
            parse_row(row)
            if row_num != (sheet_rows - 2):
                SQL += ","
            SQL += "\n"
    SQL += ");\n"


def main():
    path_list = [
        "/Users/darcy/Documents/work/sanyi/TB_MZ_GHMXB.xlsx",
        "/Users/darcy/Documents/work/sanyi/TB_HZXX.xlsx",
        "/Users/darcy/Documents/work/sanyi/TB_MZ_CFZB.xlsx",
        "/Users/darcy/Documents/work/sanyi/TB_MZ_JSMXB.xlsx",
        "/Users/darcy/Documents/work/sanyi/TB_MZ_JSZFFSMXB.xlsx",
        "/Users/darcy/Documents/work/sanyi/TB_MZ_JZMXB.xlsx",
        "/Users/darcy/Documents/work/sanyi/TB_MZ_QTCFMX.xlsx",
        "/Users/darcy/Documents/work/sanyi/TB_MZ_SFMXB.xlsx",
        "/Users/darcy/Documents/work/sanyi/TB_MZ_YPCFMX.xlsx",
        "/Users/darcy/Documents/work/sanyi/TB_MZ_ZDMXB.xlsx"
    ]

    for path in path_list:
        data = xlrd.open_workbook(path)
        (filepath, tempfilepager) = os.path.split(path)
        (table_name, extension) = os.path.splitext(tempfilepager)
        sheet = data.sheets()[0]
        print("Start parse " + path)
        parse_sheet(sheet, table_name, 2)
    write_file(SQL, "sanyi_oracle.sql")


if __name__ == '__main__':
    main()
