import FWCore.ParameterSet.Config as cms

triggerSummaryProducerAOD = cms.EDProducer('TriggerSummaryProducerAOD',
  processName = cms.string('@'),
  moduleLabelPatternsToMatch = cms.vstring('hlt*'),
  moduleLabelPatternsToSkip = cms.vstring()
)
