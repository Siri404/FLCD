class HashTable(object):
    def __init__(self, length=16):
        self.__length = length
        self.__array = [[] for x in range(length)]

    def hash(self, key):
        return (hash(key) % 2029) + 1

    def add(self, key):
        if self.is_full():
            self.double()
        index = self.hash(key) % self.__length
        if key not in self.__array[index]:
            self.__array[index].append(key)
        return index

    def search(self, key):
        index = self.hash(key)
        if key in self.__array[index]:
            return True
        return False

    def is_full(self):
        items = 0
        for item in self.__array:
            if len(item) > 0:
                items += 1
        return items > len(self.__array)/2

    def double(self):
        ht2 = HashTable(length=len(self.__array)*2)
        for i in range(self.__length):
            for key in self.__array[i]:
                ht2.add(key)

        self.__array = ht2.__array

    def __str__(self):
        return str(self.__array)
