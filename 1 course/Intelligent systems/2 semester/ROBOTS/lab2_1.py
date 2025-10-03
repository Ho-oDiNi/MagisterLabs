def sysCall_init():
    sim = require('sim')
    
    self.manipulatorID=sim.getObject("./../..")
    
    self.targetSphereID=sim.getObject("/IRB140/link1_visible/manipulationSphereBase/manipulationSphere")
    self.cubeID=sim.getObject("/DummyH/Cuboid")
    
    self.firstColumn=sim.getObject("/DummyH/Cylinder[0]")
    self.secondColumn=sim.getObject("/DummyH/Cylinder[1]")
    
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
            new_position = [target_pos[0] + offset, manipulator_pos[1], manipulator_pos[2]]
        elif coord == 'Y':
            new_position = [manipulator_pos[0], target_pos[1] + offset, manipulator_pos[2]]
        elif coord == 'XY':
            new_position = [target_pos[0] + offset, target_pos[1] + offset, manipulator_pos[2]]
        elif coord == 'Z':
            new_position = [manipulator_pos[0], manipulator_pos[1], target_pos[2]*multiply + offset]
        else:
            print(f"Warning: Invalid coordinate '{coord}'. No movement performed.")
            return

        sim.setObjectPosition(manipulatorID, -1, new_position)
        sim.wait(self.pauseValue)

    return inner
    
    
def activateManipulator(isOpen):
    sim.setInt32Signal (f"BaxterGripperWithGUI__{self.manipulatorID}___close",  int(isOpen))
    sim.wait(self.pauseValue)


def takeCube(manipulatorID, targetID):
    moveOrientation(manipulatorID, targetID)
    moveTarget = movePosition(manipulatorID, targetID)
    moveTarget('XY')
    moveTarget('Z', 0.13)
    moveTarget('Z', 0.1)
    activateManipulator(True)


def putOnColumn(manipulatorID, targetID):
    moveTarget = movePosition(manipulatorID, targetID)
    moveTarget('Z', 0.15, 2)
    moveTarget('XY')
    activateManipulator(False)


def sysCall_thread():
    print(f"BaxterGripperWithGUI__{self.manipulatorID}___close")
    takeCube(self.targetSphereID, self.cubeID)
    putOnColumn(self.targetSphereID, self.firstColumn)
    takeCube(self.targetSphereID, self.cubeID)
    putOnColumn(self.targetSphereID, self.secondColumn)
    