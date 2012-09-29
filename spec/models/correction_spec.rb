require 'spec_helper'

describe Correction do
  it "requires current text" do
    corr = FactoryGirl.build(:correction, :current => nil)
    corr.should_not be_valid
    corr.errors.should be_added(:current, :blank)
  end
end
