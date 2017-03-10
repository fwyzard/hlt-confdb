import FWCore.ParameterSet.Config as cms

pixelQuadrupletMergerEDProducer = cms.EDProducer('PixelQuadrupletMergerEDProducer',
  triplets = cms.InputTag('hitTripletMergerEDProducer'),
  ttrhBuilderLabel = cms.string('PixelTTRHBuilderWithoutAngle'),
  mergeTriplets = cms.bool(True),
  addRemainingTriplets = cms.bool(False),
  layerList = cms.PSet(),
  SeedComparitorPSet = cms.PSet(
    ComponentName = cms.string('none')
  ),
  SeedCreatorPSet = cms.PSet(
    ComponentName = cms.string('none')
  )
)
