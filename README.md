# Distributed-System-coursework

In this coursework, we were given the following task: 

to implement a distributed set of files that would use remote procedure calls (RPCs) to perform block matrix multiplications. A client would receive two matrices of arbitrary size, where blocks of the two matrices would be passed forward to a set number of servers. The servers would perform a matrix multiplication and send the result back to the client. The client would then have to position the received blocks in the right order to yield a correct overall result. All RPCs were done asynchronously; meaning that no individual call had to wait for any other call to finish first (before starting).
