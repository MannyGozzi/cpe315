tests:
	javac ./src/*.java
	java ./src/lab4.java ./test1.asm ./test1.script > ./test1.diff
	java ./src/lab4.java ./test2.asm ./test2.script > ./test2.diff
	java ./src/lab4.java ./fib10.asm ./fib10.script > ./fib10.diff
	java ./src/lab4.java ./fib20.asm ./fib20.script > ./fib20.diff

clean:
	rm -f *.diff
	rm -f *.class