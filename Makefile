CC = javac

OBJS = Main.java
EXEC = Main

$(EXEC): $(OBJS)
	$(CC) $(OBJS)

execute:
	java Main

clean:
	rm -f *.class