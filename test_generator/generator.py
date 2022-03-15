with open("tr_input.txt", "r") as source:
    lines = source.readlines()
    input_number = 0
    recover_strings = ""
    serial_strings = ""
    for line in lines:
        input_number += 1
        actions = line.split(";")
        filename = "input_" + str(input_number)
        recoverable = "true"
        serializable = "true"
        if len(actions[-1].strip()) == 0:
            actions = actions[:-1]
        if actions[-1].strip()[0] == '!':
            items = actions[-1].split(" ")
            actions = actions[:-1]
            for i in range(1, len(items)):
                if items[i].strip() == "R":
                    recoverable = "false"
                elif items[i].strip() == "CS":
                    serializable = "false"
        with open(f"./output/{filename}.txt", "w") as target:
            for action in actions:
                op = None
                index = None
                transaction = None
                action = action.strip()
                op = action[0]
                transaction = int(action[1]) + 1
                if len(action) == 2:
                    index = 0
                else:
                    index = int(action[4])
                target.write(f'{transaction} {op} {index}\n')
        recover_strings += f'"{filename}, 3, {recoverable}",\n'
        serial_strings += f'"{filename}, 3, {serializable}",\n'
print(recover_strings)
print(serial_strings)
