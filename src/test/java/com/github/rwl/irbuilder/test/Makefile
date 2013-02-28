EXECUTABLE=hello
LLVM_SUFFIX=-3.1

all: hello.ll
	llvm-as$(LLVM_SUFFIX) hello.ll
	llc$(LLVM_SUFFIX) hello.bc
	gcc -o $(EXECUTABLE) hello.s
	./$(EXECUTABLE)

clean:
	rm $(EXECUTABLE)
	rm hello.s
	rm hello.bc
