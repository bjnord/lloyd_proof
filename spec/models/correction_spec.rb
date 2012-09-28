require 'spec_helper'

describe Correction do
  it "can be saved" do
    corr = FactoryGirl.build(:correction, :current => "I think, theirfour I am.")
    corr.save.should be_true
  end
end
