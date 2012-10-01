require 'spec_helper'

describe Correction do
  it "requires current text" do
    corr = FactoryGirl.build(:correction, :current_text => nil)
    corr.should_not be_valid
    corr.errors.should be_added(:current_text, :blank)
  end
end
