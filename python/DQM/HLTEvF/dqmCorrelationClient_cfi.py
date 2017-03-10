import FWCore.ParameterSet.Config as cms

dqmCorrelationClient = cms.EDAnalyzer('TriggerRatesMonitorClient',
  dqmPath = cms.untracked.string('HLT/Datasets')
)
