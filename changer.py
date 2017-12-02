file = open("modifiedlong.json", "r")
date_dict = {}
newstr = "{"
count = 0
for line in file.readlines():
	if count % 10 == 2:
		date = line.split(": ")[1].replace(",", "").replace("\"", "")
		year = date.split(" ")[0].split("-")[0]
		month = date.split(" ")[0].split("-")[1]
		key = year + "-" + month
		if not key in date_dict:
			date_dict[key] = 1
		else:
			date_dict[key] = 1 + date_dict[key]
	count += 1
for key in date_dict:
	newstr += "\"" + key + "\": " + str(date_dict[key]) + ",\n"
newstr += "}"
print(newstr)
file.close()
writefile = open("newfile.json", "w")
writefile.write(newstr)
writefile.close()
print("Done with count " + str(count))
