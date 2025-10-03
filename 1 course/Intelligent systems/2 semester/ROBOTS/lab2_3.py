def sysCall_init():
    sim = require('sim')
    self.obj = sim.getObject("/IRB140/link1_visible/manipulationSphereBase/manipulationSphere")



def sysCall_thread():
    startPointPos  = [+0.25898, -0.72999, +0.57161]
    finishPointPos = [-0.18602, -0.31499, +0.57161]
    
    sim.setObjectPosition(self.obj, -1, startPointPos)
    
    dX = finishPointPos[0] - startPointPos[0]
    dY = finishPointPos[1] - startPointPos[1]
    N = 6
    stepX = dX/N
    stepY = dY/N
    
    for stepRow in range (0, N+1):
        if (stepRow>0):
            currPos = sim.getObjectPosition(self.obj, -1)
            currPos[1] += stepY
            sim.setObjectPosition(self.obj, -1, currPos)
            sim.wait(2)
            
        for stepCol in range (0, N):
            currPos = sim.getObjectPosition(self.obj, -1)
            if stepRow%2 == 0:
                currPos[0] += stepX
            else:
                currPos[0] -= stepX
            sim.setObjectPosition(self.obj, -1, currPos)
            sim.wait(2)
