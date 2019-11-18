from model.FiniteAutomaton import FiniteAutomaton
from model.Grammar import Grammar


class Command:
    def __init__(self, cmd, operation):
        self.__cmd = cmd
        self.__operation = operation

    def executeWithParams(self, params):
        self.__operation(params)

    def execute(self):
        self.__operation()

    def __str__(self):
        return self.__cmd


class Console:

    def __readCommand(self):
        cmd = input(">> ").strip().split()

        return cmd[0], cmd[1:]

    def __helpCommand(self, opt1, opt2):
        print("The available commands are:")
        for cmd in opt1:
            print('\t' + str(opt1[cmd]))
        for cmd in opt2:
            print('\t' + str(opt2[cmd]))
        print("\t help")
        print("\t exit")

    def __init__(self):
        self.grammar = Grammar.readFromFile("RegularGrammar.txt")
        self.finiteAutomaton = FiniteAutomaton.readFromFile("FiniteAutomaton.txt")

    def __uiReadGrammar(self, params):
        if params[0] == "file":
            self.grammar = Grammar.readFromFile("RegularGrammar.txt")
        elif params[0] == "input":
            self.grammar = Grammar.readFromConsole()
        else:
            "Parameter should be file or input!"

    def __uiDisplayGrammar(self, params):
        self.grammar.printElement(params[0])

    def __uiIsRegular(self):
        print(self.grammar.isRegular())

    def __uiReadFA(self, params):
        if params[0] == "file":
            self.finiteAutomaton = FiniteAutomaton.readFromFile("FiniteAutomaton.txt")
        elif params[0] == "input":
            self.finiteAutomaton = FiniteAutomaton.readFromConsole()
        else:
            "Parameter should be file or input!"

    def __uiDisplayFA(self, params):
        self.finiteAutomaton.printElement(params[0])

    def __uiConstructRG(self):
        if self.grammar.isRegular():
            self.grammar = self.finiteAutomaton.toRegularGrammar()
        else:
            print("Current grammar is not regular!")

    def __uiConstructFA(self):
        self.finiteAutomaton = self.grammar.toFiniteAutomaton()

    def run(self):
        optionsWithParams = {'readGrammar': Command('readGrammar <source>', self.__uiReadGrammar),
                             'displayGrammar': Command('displayGrammar <element>', self.__uiDisplayGrammar),
                             'readFA': Command('readFA <source>', self.__uiReadFA),
                             'displayFA': Command('displayFA <element>', self.__uiDisplayFA)
                             }

        options = {'isRegular': Command('isRegular', self.__uiIsRegular),
                   'constructRG': Command('constructRG', self.__uiConstructRG),
                   'constructFA': Command('constructFA', self.__uiConstructFA)
                   }

        while True:
            cmd = self.__readCommand()

            if cmd[0] == 'exit':
                return

            if cmd[0] in optionsWithParams:
                optionsWithParams[cmd[0]].executeWithParams(cmd[1])
            elif cmd[0] in options:
                options[cmd[0]].execute()
            elif cmd[0] == 'help':
                self.__helpCommand(optionsWithParams, options)
            else:
                print("Invalid Command!")
