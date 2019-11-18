from Scanner import Scanner

errors, ST, PIF = Scanner.analyze("input.txt")
if len(errors) > 0:
    print(errors)
else:
    print("ST:")
    print(ST)
    print("PIF:")
    print(PIF)
