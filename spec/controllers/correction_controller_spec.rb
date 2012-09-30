require 'spec_helper'

describe CorrectionController do

  describe 'index action' do
    it 'returns HTTP success' do
      get :index
      response.should be_success
    end

    it 'returns an array of corrections' do
      FactoryGirl.create(:correction)
      popeye = 'I yam what I yam.'
      FactoryGirl.create(:correction, :current => popeye)
      get :index
      assigns[:corrections].should have_at_least(2).items
      assigns[:corrections].collect{|c| c.current}.should include(popeye)
    end
  end

  describe 'sync action' do
    before(:all) do
      @corrections = [
        {'current' => 'irregardless'},
        {'current' => 'equally as'},
        {'current' => 'pour over'}
      ]
    end

    it 'returns HTTP success' do
      post :sync, :corrections => @corrections, :format => :json
      response.should be_success
    end

    it 'accepts valid corrections' do
      post :sync, :corrections => @corrections, :format => :json
      JSON.parse(response.body).each do |status|
        status['status'].should == 'ok'
      end
    end

    it 'rejects invalid corrections' do
      @corrections[0]['current'] = nil
      @corrections[1]['current'] = ''
      @corrections[2].delete('current')
      post :sync, :corrections => @corrections, :format => :json
      JSON.parse(response.body).each do |status|
        status['status'].should == 'error'
      end
    end
  end

end
