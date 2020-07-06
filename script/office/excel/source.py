import os
import xlrd
import time
import requests
import json


def parse(sheet, row_begin, url, token):
    sheet_rows = sheet.nrows
    temp = ''
    for row_num in range(row_begin, sheet_rows):
        row = sheet.row_values(row_num)
        first = str(row[0])
        second = str(row[1])
        if first != '':
            temp = first
        if first == '' and temp != '':
            first = temp
        if second == '':
            continue
        body = {
            'parentSourceId': first,
            'sourceId': second
        }
        headers = {
            'token': token,
            'Content-Type': 'application/json;charset=UTF-8'
        }
        print("body: " + body.__str__())
        time.sleep(3)
        response = requests.post(url, data=json.dumps(body), headers=headers)
        print(response.text)


def main():
    path_list = [
        "1.xls"
    ]

    for path in path_list:
        data = xlrd.open_workbook(path)
        sheet = data.sheets()[0]
        parse(sheet, 1,
              '',
              '')


if __name__ == '__main__':
    main()
