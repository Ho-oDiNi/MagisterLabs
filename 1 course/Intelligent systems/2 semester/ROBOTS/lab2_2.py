def sysCall_init():
    sim = require('sim')
    
    self.manipulatorID=sim.getObject("./../..")
    
    self.targetSphereID=sim.getObject("/IRB140/link1_visible/manipulationSphereBase/manipulationSphere")
    self.cubeID=sim.getObject("/Dummy/Cuboid")
    
    self.firstColumn=sim.getObject("/Dummy/BoxDst[0]")
    self.secondColumn=sim.getObject("/Dummy/BoxDst[1]")
    
    
    
    self.pauseValue = 2


def moveOrientation(manipulatorID, targetID):
    orientation = sim.getObjectOrientation(targetID)
    sim.setObjectOrientation(manipulatorID, -1, orientation)
    sim.wait(self.pauseValue)


def movePosition(manipulatorID, targetID):
    def inner(coord, offset=0, multiply=1):
        target_pos = sim.getObjectPosition(targetID)
        manipulator_pos = sim.getObjectPosition(manipulatorID)

        if coord == 'X':
            new_position = [target_pos[0]*multiply + offset, manipulator_pos[1], manipulator_pos[2]]
        elif coord == 'Y':
            new_position = [manipulator_pos[0], target_pos[1]*multiply + offset, manipulator_pos[2]]
        elif coord == 'XY':
            new_position = [target_pos[0]*multiply + offset, target_pos[1]*multiply + offset, manipulator_pos[2]]
        elif coord == 'Z':
            new_position = [manipulator_pos[0], manipulator_pos[1], target_pos[2]*multiply + offset]
        else:
            print(f"Warning: Invalid coordinate '{coord}'. No movement performed.")
            return

        sim.setObjectPosition(manipulatorID, -1, new_position)
        sim.wait(self.pauseValue)

    return inner
    
    
def activateManipulator(isOpen):
    sim.setInt32Signal (f"BaxterVacuumCupWhithGUI__{self.manipulatorID}___active",  int(isOpen))
    sim.wait(self.pauseValue)


def takeCube(manipulatorID, targetID):
    moveOrientation(manipulatorID, targetID)
    moveTarget = movePosition(manipulatorID, targetID)
    moveTarget('Z', 0.01)
    moveTarget('XY', -0.068)
    activateManipulator(True)


def putOnColumn(manipulatorID, targetID):
    moveTarget = movePosition(manipulatorID, targetID)
    moveTarget('XY', -0.01)
    moveOrientation(manipulatorID, targetID)
    moveTarget('Z')
    moveTarget('XY')
    activateManipulator(False)


def moveToPos(position):
    sim.setObjectPosition(self.targetSphereID, -1, position)
    sim.wait(self.pauseValue)


def orientToPos(orientation):
    sim.setObjectOrientation(self.targetSphereID, -1, orientation)
    sim.wait(self.pauseValue)
    
 
def takeCubeFirst():
    moveToPos([0.42898, 0.24501, 0.65161])
    moveOrientation(self.targetSphereID, self.cubeID)
    moveToPos([0.54484, 0.44623, 0.505])
    activateManipulator(True)
    

def putCubeFirst():
    moveToPos([0.42898, 0.24501, 0.65161])
    moveToPos([-0.37602, 0.45001, 0.65161])
    orientToPos([-1.57, 0.872665, 0])
    moveToPos([-0.27293, -0.24282, 0.753])
    moveToPos([-0.40293, -0.32782, 0.753])


def putCubeSecond():
    moveToPos([-0.27293, -0.24282, 0.753])
    moveToPos([0.09856, -0.46149, +0.62339])
    orientToPos([3.14*(-40)/180, 3.14*(-30)/180, 3.14*(30)/180])
    moveToPos([+0.34356, -0.40149, +0.61339])
    moveToPos([0.39856, -0.48149, 0.49839])
    

def sysCall_thread():
    takeCubeFirst()
    putCubeFirst()
    putCubeSecond()
    
    
#[+0.34356, -0.40149, +0.61339]
#[0.38856, -0.48649, 0.49839]
#[0.45856, ?0.546485, 0.41339]



#[-0,698132, -0,523599, 0,523599]