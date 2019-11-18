import re
import LexicalRules as lr
from HashTable import HashTable
from CodificationTable import CodificationTable


class Scanner:
    """
    Input: file name of text file with mini-language source code
    Output: PIF, ST - Program internal form, Symbol table
    """
    @staticmethod
    def analyze(filename):
        fileHandler = open(filename, "r")
        fileContent = fileHandler.readlines()
        PIF = []
        ST = HashTable()
        errors = ""
        for i in range(len(fileContent)):
            line = re.split("(" + lr.separators + ")", fileContent[i])
            for token in line:
                token = token.strip("\n")
                if token == '':
                    continue
                if re.match(lr.keywords, token) or re.match(lr.separators, token) or re.match(lr.operators, token):
                    PIF.append([CodificationTable[token], -1])
                elif re.match(lr.identifier, token):
                    pos = ST.add(token)
                    PIF.append([CodificationTable['identifier'], pos])
                elif re.match(lr.integer, token):
                    token = int(token)
                    pos = ST.add(token)
                    PIF.append([CodificationTable['constant'], pos])
                elif re.match(lr.string, token) or re.match(lr.ArrayList, token):
                    pos = ST.add(token)
                    PIF.append([CodificationTable['constant'], pos])
                else:
                    errors += "Lexical error on line " + str(i) + ":\n"
                    error = re.split("(" + token + ")", fileContent[i])
                    errors += error[0] + error[1] + "\n"
                    errors += " "*(len(error[0])+len(error[1])) + "^"
                    return errors, ST, PIF

        if PIF[0][0] != 32 or PIF[-1][0] != 33:
            errors += "Error:\nCode should be between the ~Start and ~End tokens"

        return errors, ST, PIF
