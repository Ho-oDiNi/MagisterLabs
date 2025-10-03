def setWheelSpeedForDir (direction, speed_RadPerSec):
    if direction> 100:
        direction =  100
    if direction<-100:
        direction = -100

    alpha, beta, gamma = sim.getObjectOrientation(self.steeringWheelBaseId, self.vehicleId)
    gamma = direction*0.0236
    sim.setObjectOrientation(self.steeringWheelBaseId, self.vehicleId, [alpha, beta, gamma])


    kLeft = 0.02
    kRight = -kLeft
    b     = 1
    if direction>=0:
        powerL = 1
        powerR = (direction*kRight  + b)
    else:
        powerL = (direction*kLeft + b)
        powerR = 1

    LeftAngleSpeed_RadPerSec  = speed_RadPerSec* powerL
    RightAngleSpeed_RadPerSec = speed_RadPerSec* powerR


    sim.setJointTargetVelocity(self.leftMotorId,  LeftAngleSpeed_RadPerSec)
    sim.setJointTargetVelocity(self.rightMotorId, RightAngleSpeed_RadPerSec)



def sysCall_init():
    sim = require('sim')
    self.steeringWheelBaseId =  sim.getObject("/VehicleBase/SteeringDial/SteeringWheelBase")
    self.vehicleId      =  sim.getObject("/VehicleBase")
    self.leftMotorId = sim.getObject("/VehicleBase/revj_WL")  
    self.rightMotorId = sim.getObject("/VehicleBase/revj_WR")
    self.leftVisId =sim.getObject("/VehicleBase/visSensors/vis_sensor_L1")
    self.rightVisId=sim.getObject("/VehicleBase/visSensors/vis_sensor_R1")


def sysCall_thread():
    sim.setStepping(True)
    sim.handleVisionSensor(sim.handle_all)

    baseSpeed_RadPerSec = 5

    currentTime  = sim.getSimulationTime()
    
    KP                 = 100
    current_error      =  0
    
    previous_error     =  0
    KD                 =  5

    
    while True:
        leftVisAnswer  = sim.readVisionSensor(self.leftVisId)
        rightVisAnswer = sim.readVisionSensor(self.rightVisId)

        # Left visSensor
        leftVisArray      = leftVisAnswer [1]
        leftAvgBrightness = leftVisArray [10]

        # Right visSensor
        rightVisArray      = rightVisAnswer [1]
        rightAvgBrightness = rightVisArray [10]
        

        previousTime = currentTime
        currentTime  = sim.getSimulationTime()
        deltaTime    = currentTime-previousTime
        
        if deltaTime > 0:
            # proportional + diff part
            previous_error = current_error

            current_error = 0-(rightAvgBrightness-leftAvgBrightness)
            propPart      = current_error     * KP
            diffPart = ((current_error-previous_error)/deltaTime) * KD


            newDir        = propPart + diffPart
            setWheelSpeedForDir (newDir, baseSpeed_RadPerSec)
            print("p={:.3f}, d={:.3f}".format(propPart, diffPart))

            
        sim.step()

