def message_analysis(message):
    uppercase=0
    punctuation=0
    total_char=0
    caps_ratio=0.0
    is_reapeated_char=False
    previous_char=""
    repated_char_len=0
    classification=""

    for character in message:
        if 'A' <= character <= 'Z':
            uppercase+=1
            total_char+=1
        if 'a' <= character <= 'z':
            total_char+=1
        if character == '?' or character == '!':
            punctuation+=1
        if character != previous_char:
            repated_char_len = 1
            previous_char = character

        else:
            repated_char_len +=1
            if repated_char_len >3:
                is_reapeated_char = True

        if total_char == 0:
            caps_ratio = 0
        else:
            caps_ratio = uppercase/total_char
        
        if caps_ratio >= 0.6 or punctuation >= 5:
            classification = "AGGRESSIVE"
        elif caps_ratio >= 0.3 or punctuation >= 3:
            classification = "URGENT"
        else:
            classification = "CALM"
    
    return classification


print(message_analysis(" "))
print(message_analysis("Hey, want to connect?"))
print(message_analysis("hi!!!"))
print(message_analysis("PLEASE ACCEPT MY REQUEST!!!"))
print(message_analysis("heyyyyy"))
