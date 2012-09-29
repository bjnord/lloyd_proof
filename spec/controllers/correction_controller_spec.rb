require 'spec_helper'

describe CorrectionController do

  describe "index action" do
    it 'returns HTTP success' do
      get 'index'
      response.should be_success
    end

    it 'returns an array of corrections' do
      FactoryGirl.create(:correction)
      popeye = 'I yam what I yam.'
      FactoryGirl.create(:correction, :current => popeye)
      get 'index'
      assigns[:corrections].should have_at_least(2).items
      assigns[:corrections].collect{|c| c.current}.should include(popeye)
    end
  end

end
