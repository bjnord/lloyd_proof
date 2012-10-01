require 'spec_helper'

describe "corrections/index.html.erb" do
  before(:all) do
    @corrections = FactoryGirl.build_list(:correction, 2)
  end

  it "renders all corrections" do
    render
    @corrections.each do |correction|
      rendered.should contain(correction.current)
    end
  end
end
