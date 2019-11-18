class Grammar:

    def __init__(self, N, E, P, S):
        self.N = N
        self.E = E
        self.P = P
        self.S = S

    @staticmethod
    def parseLine(line):
        return [value.strip() for value in line.strip().split('=')[1].strip()[1:-1].strip().split(',')]

    @staticmethod
    def parseConsole(line):
        return [value.strip() for value in line.strip()[1:-1].strip().split(',')]

    @staticmethod
    def parseRules(rules):
        result = []

        for rule in rules:
            lhs, rhs = rule.split('->')
            lhs = lhs.strip()
            rhs = [value.strip() for value in rhs.split('|')]

            for value in rhs:
                result.append((lhs, value))

        return result

    @staticmethod
    def readFromFile(fileName):
        with open(fileName) as file:
            N = Grammar.parseLine(file.readline())
            E = Grammar.parseLine(file.readline())
            S = file.readline().split('=')[1].strip()
            P = Grammar.parseRules(Grammar.parseLine(''.join([line for line in file])))

            return Grammar(N, E, P, S)

    @staticmethod
    def readFromConsole():
        N = Grammar.parseConsole(input('N = '))
        E = Grammar.parseConsole(input('E = '))
        S = input('S = ')
        P = Grammar.parseRules(Grammar.parseConsole(input('P = ')))

        return Grammar(N, E, P, S)

    def printElement(self, element):
        if element == "P":
            productions = "P = {\n"
            for prod in self.P:
                productions += prod[0] + " -> " + prod[1] + ",\n"
            productions = productions[:-2] + "\n}"
            print(productions)
            return
        elif element == "N":
            myList = self.N
        elif element == "E":
            myList = self.E
        elif element == "S":
            print("S = " + self.S)
            return
        else:
            print("Valid elements: 'N', 'E', 'P', 'S'")
            return
        string = element + " = {"
        for value in myList:
            string += value + ", "
        string = string[:-2] + "}"
        print(string)

    def toFiniteAutomaton(self):
        S = []
        F = ['K']

        for production in self.P:
            state2 = 'K'
            state1, rhs = production
            if state1 == self.S and rhs[0] == 'E':
                F.append(self.S)
                continue

            route = rhs[0]
            if len(rhs) == 2:
                state2 = rhs[1]
            S.append(((state1, route), state2))

        from model import FiniteAutomaton
        return FiniteAutomaton.FiniteAutomaton(self.N + ['K'], self.E, S, self.S, F)

    def isRegular(self):
        usedInRhs = dict()
        notAllowedInRhs = dict()

        for rule in self.P:
            lhs, rhs = rule
            terminalFirst = False
            nonTerminalFirst = False
            if len(rhs) > 2:
                return False
            for char in rhs:
                if char in self.N:
                    usedInRhs[char] = True
                    nonTerminalFirst = True
                elif char in self.E:
                    if nonTerminalFirst:
                        return False
                    terminalFirst = True
                if char == 'E':
                    notAllowedInRhs[lhs] = True

            if nonTerminalFirst and not terminalFirst:
                return False

        for char in notAllowedInRhs.keys():
            if char in usedInRhs:
                return False

        return True
