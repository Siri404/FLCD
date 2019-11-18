
class FiniteAutomaton:

    def __init__(self, Q, E, T, q0, F):
        self.Q = Q
        self.E = E
        self.T = T
        self.q0 = q0
        self.F = F

    @staticmethod
    def parseLine(line):
        return [value.strip() for value in line.strip().split('=')[1].strip()[1:-1].strip().split(',')]

    @staticmethod
    def parseConsole(line):
        return [value.strip() for value in line.strip()[1:-1].strip().split(',')]

    @staticmethod
    def parseTransitions(line):
        transitions = line.strip().split('=')[1].split(";")
        transitions[0] = transitions[0][2:]
        transitions[-1] = transitions[-1][:-1]
        result = []

        for transition in transitions:
            lhs, rhs = transition.split('->')
            state2 = rhs.strip()
            state1, route = [value.strip() for value in lhs.strip()[1:-1].split(',')]
            result.append(((state1, route), state2))

        return result

    @staticmethod
    def readFromFile(fileName):
        with open(fileName) as file:
            Q = FiniteAutomaton.parseLine(file.readline())
            E = FiniteAutomaton.parseLine(file.readline())
            q0 = file.readline().split('=')[1].strip()
            F = FiniteAutomaton.parseLine(file.readline())
            T = FiniteAutomaton.parseTransitions((''.join([line for line in file])))

            return FiniteAutomaton(Q, E, T, q0, F)

    @staticmethod
    def readFromConsole():
        Q = FiniteAutomaton.parseConsole(input('Q = '))
        E = FiniteAutomaton.parseConsole(input('E = '))
        q0 = input('q0 = ')
        F = FiniteAutomaton.parseConsole(input('F = '))

        T = FiniteAutomaton.parseTransitions("T = " + (input('T = ')))

        return FiniteAutomaton(Q, E, T, q0, F)

    def printElement(self, element):
        if element == "T":
            transitions = "T = {\n"
            for trans in self.T:
                transitions += "(" + trans[0][0] + ", " + trans[0][1] + ")" + " -> " + trans[1] + ";\n"
            transitions = transitions[:-2] + "\n}"
            print(transitions)
            return
        elif element == "Q":
            myList = self.Q
        elif element == "E":
            myList = self.E
        elif element == "F":
            myList = self.F
        elif element == "q0":
            print("q0 = " + self.q0)
            return
        else:
            print("Valid elements: 'T', 'Q', 'E', 'F', 'q0'")
            return
        string = element + " = {"
        for value in myList:
            string += value + ", "
        string = string[:-2] + "}"
        print(string)

    def toRegularGrammar(self):
        P = []

        if self.q0 in self.F:
            P.append((self.q0, 'E'))

        for transition in self.T:
            lhs, state2 = transition
            state1, route = lhs

            P.append((state1, route + state2))

            if state2 in self.F:
                P.append((state1, route))

        from model.Grammar import Grammar
        return Grammar(self.Q, self.E, P, self.q0)
